package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.MatchMakingResult;
import fr.tobby.tripnjoyback.model.MatchMakingUserModel;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.RangeAnswerModel;
import fr.tobby.tripnjoyback.notification.SavedNotificationService;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MatchMaker {

    private static final Logger logger = LoggerFactory.getLogger(MatchMaker.class);
    private static final float MINIMAL_MATCHING_SCORE = 5f;

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final MatchMakerScoreComputer scoreComputer;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final ProfileService profileService;
    private final SavedNotificationService notificationService;
    private final ReportService reportService;

    private long taskIndex = 1L;
    private final Map<Long, CompletableFuture<MatchMakingResult>> tasks = new HashMap<>();

    public MatchMaker(final ProfileRepository profileRepository, final UserRepository userRepository, final MatchMakerScoreComputer scoreComputer,
                      final GroupService groupService, final GroupRepository groupRepository,
                      final ProfileService profileService, final SavedNotificationService notificationService,
                      final ReportService reportService)
    {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.scoreComputer = scoreComputer;
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.profileService = profileService;
        this.notificationService = notificationService;
        this.reportService = reportService;
    }

    @Transactional
    public long match(@NotNull UserEntity entity, long profileId) throws IllegalStateException
    {
        ProfileEntity profile = profileRepository.findById(profileId).orElseThrow(ProfileNotFoundException::new);
        profileService.setProfileInactive(entity.getId());
        profileService.setActiveProfile(profileId, true);
        return match(entity, profileService.getProfile(profile));
    }

    @Transactional
    public long match(@NotNull UserEntity entity, @NotNull ProfileModel profile) throws IllegalStateException
    {
        final CompletableFuture<MatchMakingResult> task = this.match(MatchMakingUserModel.from(entity, profile));
        tasks.put(taskIndex, task);
        return taskIndex++;
    }

    @NotNull
    public MatchMakingResult getTask(long taskId) throws NoSuchElementException, ExecutionException, InterruptedException
    {
        CompletableFuture<MatchMakingResult> task = tasks.get(taskId);
        if (task == null)
            throw new NoSuchElementException();
        return task.isDone() ? task.get() : new MatchMakingResult(MatchMakingResult.Type.SEARCHING, null);
    }

    @Transactional
    @Async
    public CompletableFuture<MatchMakingResult> match(@NotNull final MatchMakingUserModel user)
    {
        logger.info("Starting matchmaking for user {}", user.getUserId());
        Optional<GroupEntity> matchedGroup = findMatchingGroup(user);

        if (matchedGroup.isPresent())
        {
            GroupEntity group = matchedGroup.get();
            logger.info("User {} joining group {}", user.getUserId(), group.getId());
            profileService.setActiveProfile(user.getProfile().getId(), false);
            groupService.addUserToPublicGroup(group.getId(), user.getUserId(), user.getProfile().getId());
            notificationService.sendToGroup(group.getId(),
                    "Nouveau membre",
                    String.format("%s a rejoint l'aventure !", userRepository.findById(user.getUserId()).orElseThrow().getFirstname()),
                    Map.of("newMemberId", String.valueOf(user.getUserId()),
                            "groupId", String.valueOf(group.getId())));
            return CompletableFuture.completedFuture(new MatchMakingResult(MatchMakingResult.Type.JOINED, GroupModel.of(group)));
        }

        Optional<MatchMakingUserModel> matchingUser = findMatchingUser(user);
        if (matchingUser.isPresent())
        {
            MatchMakingUserModel matched = matchingUser.get();
            logger.info("Creating new group with user {} and user {}", user.getUserId(), matched.getUserId());
            RangeAnswerModel sizeRange = scoreComputer.computeCommonRange(user.getProfile().getGroupSize(), matched.getProfile().getGroupSize()).orElseThrow();
            int maxSize = (sizeRange.getMaxValue() + sizeRange.getMinValue()) / 2;

            UserEntity userEntity = userRepository.getById(user.getUserId());
            UserEntity matchedEntity = userRepository.getById(matched.getUserId());


            ProfileEntity groupProfile = this.computeGroupProfile(user.getProfile(), matched.getProfile());
            GroupModel created = groupService.createPublicGroup(userEntity,
                    profileRepository.getById(user.getProfile().getId()),
                    matchedEntity,
                    profileRepository.getById(matched.getProfile().getId()),
                    maxSize,
                    groupProfile);

            matchedEntity.setWaitingForGroup(false);
            profileService.setActiveProfile(matched.getProfile().getId(), false);
            profileService.setActiveProfile(user.getProfile().getId(), false);
            if (matchedEntity.getFirebaseToken() != null)
            {
                notificationService.sendToUser(matchedEntity.getId(),
                        "Groupe trouvé",
                        "Un nouveau groupe de voyage a été créé",
                        Map.of("groupId", String.valueOf(created.getId())));
            }
            return CompletableFuture.completedFuture(new MatchMakingResult(MatchMakingResult.Type.CREATED, created));
        }
        else
        {
            logger.info("No match found for user {}. Set as waiting for match", user.getUserId());
            userRepository.getById(user.getUserId()).setWaitingForGroup(true);
            return CompletableFuture.completedFuture(new MatchMakingResult(MatchMakingResult.Type.WAITING, null));
        }
    }

    /**
     * Go through all groups and return the one that matches the user the best
     */
    private Optional<GroupEntity> findMatchingGroup(final @NotNull MatchMakingUserModel user)
    {
        Collection<GroupEntity> groups = groupRepository.findAvailableGroups();
        return groups.stream()
                     .map(group -> {
                         ProfileModel profileModel = profileService.getProfile(group.getProfile());
                         return new Pair<>(group, profileModel);
                     })
                     .filter(pair -> scoreComputer.isUserCompatible(pair.right(), user))
                     .map(pair -> {
                         float score = scoreComputer.computeMatchingScore(user.getProfile(), pair.right());
                         score -= reportService.getReportCountForUser(user.getUserId());
                         return new Pair<>(pair.left(), score);
                     })
                     .filter(pair -> pair.right() > MINIMAL_MATCHING_SCORE)
                     .max(Comparator.comparingDouble(Pair::right))
                     .map(Pair::left);
    }

    private Optional<MatchMakingUserModel> findMatchingUser(@NotNull MatchMakingUserModel user)
    {
        Collection<MatchMakingUserModel> others = userRepository.findAllByWaitingForGroupIsTrue()
                                                                .stream()
                                                                .map(other -> {
                                                                    ProfileModel profileModel = profileService.getActiveProfileModel(other.getId()).orElseThrow();
                                                                    return MatchMakingUserModel.from(other, profileModel);
                                                                })
                                                                .collect(Collectors.toSet());


        return others.stream()
                     .filter(other -> scoreComputer.isUserCompatible(user.getProfile(), other) && scoreComputer.isUserCompatible(other.getProfile(), user))
                     .map(other -> {
                         float score = scoreComputer.computeMatchingScore(user.getProfile(), other.getProfile());
                         score -= reportService.getReportCountForUser(user.getUserId());
                         score -= reportService.getReportCountForUser(other.getUserId());
                         return new Pair<>(other, score);
                     })
                     .filter(pair -> pair.right() > MINIMAL_MATCHING_SCORE)
                     .max(Comparator.comparingDouble(Pair::right))
                     .map(Pair::left);
    }

    private ProfileEntity computeGroupProfile(@NotNull ProfileModel profileA, @NotNull ProfileModel profileB)
    {
        List<AvailabilityAnswerModel> commonAvailabilities = scoreComputer.computeCommonAvailabilities(profileA.getAvailabilities(), profileB.getAvailabilities());

        ProfileModel groupModel = ProfileModel.builderOf(profileA)
                                              .availabilities(commonAvailabilities)
                                              .build();
        return profileService.createProfile(groupModel);
    }
}
