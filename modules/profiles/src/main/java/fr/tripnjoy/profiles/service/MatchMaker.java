package fr.tripnjoy.profiles.service;

import fr.tripnjoy.common.utils.Pair;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.profiles.entity.ProfileEntity;
import fr.tripnjoy.profiles.exception.ProfileNotFoundException;
import fr.tripnjoy.profiles.model.MatchMakingResult;
import fr.tripnjoy.profiles.model.MatchMakingUserModel;
import fr.tripnjoy.profiles.model.ProfileModel;
import fr.tripnjoy.profiles.model.answer.AvailabilityAnswerModel;
import fr.tripnjoy.profiles.model.answer.RangeAnswerModel;
import fr.tripnjoy.profiles.repository.ProfileRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.response.UserResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MatchMaker {

    private static final Logger logger = LoggerFactory.getLogger(MatchMaker.class);
    private static final float MINIMAL_MATCHING_SCORE = 5f;

    private final ProfileRepository profileRepository;
    private final MatchMakerScoreComputer scoreComputer;
    private final ProfileService profileService;
    private final UserFeignClient userFeignClient;
    private final GroupFeignClient groupFeignClient;

    private long taskIndex = 1L;
    private final Map<Long, CompletableFuture<MatchMakingResult>> tasks = new HashMap<>();

    public MatchMaker(final ProfileRepository profileRepository, final MatchMakerScoreComputer scoreComputer, final ProfileService profileService,
                      final UserFeignClient userFeignClient, final GroupFeignClient groupFeignClient)
    {
        this.profileRepository = profileRepository;
        this.scoreComputer = scoreComputer;
        this.profileService = profileService;
        this.userFeignClient = userFeignClient;
        this.groupFeignClient = groupFeignClient;
    }

    /**
     * Start a matchmaking for the given user with the given profile
     */
    @Transactional
    public long match(final long userId, long profileId) throws IllegalStateException
    {
        ProfileEntity profile = profileRepository.findById(profileId).orElseThrow(ProfileNotFoundException::new);
        profileService.setProfileInactive(userId);
        profileService.setActiveProfile(profileId, true);
        return match(userId, profileService.getProfile(profile));
    }

    @Transactional
    public long match(final long userId, @NotNull ProfileModel profile) throws IllegalStateException
    {
        UserResponse user = this.userFeignClient.getUserById(List.of("admin"), userId);
        final CompletableFuture<MatchMakingResult> task = this.match(MatchMakingUserModel.from(user, Instant.now(), profile));
        tasks.put(taskIndex, task);
        return taskIndex++;
    }

    @NotNull
    public MatchMakingResult getTask(long taskId) throws NoSuchElementException, ExecutionException, InterruptedException
    {
        CompletableFuture<MatchMakingResult> task = tasks.get(taskId);
        if (task == null)
            throw new NoSuchElementException();
        return task.isDone() ? task.get() : new MatchMakingResult(MatchMakingResult.Type.SEARCHING, 0);
    }

    @Transactional
    @Async
    public CompletableFuture<MatchMakingResult> match(@NotNull final MatchMakingUserModel user)
    {
        logger.info("Starting matchmaking for user {}", user.getUserId());
        Optional<Long> matchedGroup = findMatchingGroup(user);

        if (matchedGroup.isPresent())
        {
            long groupId = matchedGroup.get();
            logger.info("User {} joining group {}", user.getUserId(), groupId);
            profileService.setActiveProfile(user.getProfile().getId(), false);
            // FIXME: use rabbitMQ
//            groupService.addUserToPublicGroup(groupId, user.getUserId(), user.getProfile().getId());
            // FIXME: notification service
//            notificationService.sendToGroup(groupId,
//                    "Nouveau membre",
//                    String.format("%s a rejoint l'aventure !", userRepository.findById(user.getUserId()).orElseThrow().getFirstname()),
//                    Map.of("newMemberId", String.valueOf(user.getUserId()),
//                            "groupId", String.valueOf(groupId)));
            return CompletableFuture.completedFuture(new MatchMakingResult(MatchMakingResult.Type.JOINED, groupId));
        }

        Optional<MatchMakingUserModel> matchingUser = findMatchingUser(user);
        if (matchingUser.isPresent())
        {
            MatchMakingUserModel matched = matchingUser.get();
            logger.info("Creating new group with user {} and user {}", user.getUserId(), matched.getUserId());
            RangeAnswerModel sizeRange = scoreComputer.computeCommonRange(user.getProfile().getGroupSize(), matched.getProfile().getGroupSize()).orElseThrow();
            int maxSize = (sizeRange.getMaxValue() + sizeRange.getMinValue()) / 2;

//            UserEntity userEntity = userRepository.getById(user.getUserId());
//            UserEntity matchedEntity = userRepository.getById(matched.getUserId());


            ProfileEntity groupProfile = this.computeGroupProfile(user.getProfile(), matched.getProfile());
            // FIXME: rabbitMQ
//            GroupModel created = groupService.createPublicGroup(userEntity,
//                    profileRepository.getById(user.getProfile().getId()),
//                    matchedEntity,
//                    profileRepository.getById(matched.getProfile().getId()),
//                    maxSize,
//                    groupProfile);

//            matchedEntity.setWaitingForGroup(false);
            profileService.setActiveProfile(matched.getProfile().getId(), false);
            profileService.setActiveProfile(user.getProfile().getId(), false);
//            if (matchedEntity.getFirebaseToken() != null)
//            {
                // FIXME: notification service
//                notificationService.sendToUser(matchedEntity.getId(),
//                        "Groupe trouvé",
//                        "Un nouveau groupe de voyage a été créé",
//                        Map.of("groupId", String.valueOf(created.getId())));
//            }
            return CompletableFuture.completedFuture(new MatchMakingResult(MatchMakingResult.Type.CREATED, 0)); // FIXME: replace 0 with created id
        }
        else
        {
            logger.info("No match found for user {}. Set as waiting for match", user.getUserId());
            // FIXME: rabbit MQ
//            userRepository.getById(user.getUserId()).setWaitingForGroup(true);
            return CompletableFuture.completedFuture(new MatchMakingResult(MatchMakingResult.Type.WAITING, 0));
        }
    }

    /**
     * Go through all groups and return the one that matches the user the best
     */
    private Optional<Long> findMatchingGroup(final @NotNull MatchMakingUserModel user)
    {
        Collection<Long> groups = groupFeignClient.getOpenGroups();
        return groups.stream()
                     .map(group -> {
                         Optional<ProfileEntity> profile = profileRepository.findByGroupId(group);
                         if (profile.isEmpty())
                             return null;
                         ProfileModel profileModel = profileService.getProfile(profile.get());
                         return new Pair<>(group, profileModel);
                     })
                     .filter(pair -> pair != null && scoreComputer.isUserCompatible(pair.right(), user))
                     .map(pair -> {
                         float score = scoreComputer.computeMatchingScore(user.getProfile(), pair.right());
                         // FIXME: report service
//                         score -= reportService.getReportCountForUser(user.getUserId());
                         return new Pair<>(pair.left(), score);
                     })
                     .filter(pair -> pair.right() > MINIMAL_MATCHING_SCORE)
                     .max(Comparator.comparingDouble(Pair::right))
                     .map(Pair::left);
    }

    private Optional<MatchMakingUserModel> findMatchingUser(@NotNull MatchMakingUserModel user)
    {
        Collection<UserResponse> waitingUsers = userFeignClient.getUsersWaitingForGroup();
        Collection<MatchMakingUserModel> others = waitingUsers
                .stream()
                .map(waitingUser -> {
                    ProfileModel profileModel = profileService.getActiveProfileModel(waitingUser.getId()).orElseThrow();
                    return MatchMakingUserModel.from(waitingUser, Instant.now(), profileModel);
                })
                .collect(Collectors.toSet());


        return others.stream()
                     .filter(other -> scoreComputer.isUserCompatible(user.getProfile(), other) && scoreComputer.isUserCompatible(other.getProfile(), user))
                     .map(other -> {
                         float score = scoreComputer.computeMatchingScore(user.getProfile(), other.getProfile());
                         // FIXME: report service
//                         score -= reportService.getReportCountForUser(user.getUserId());
//                         score -= reportService.getReportCountForUser(other.getUserId());
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
