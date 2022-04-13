package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.AnswersEntity;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.model.MatchMakingUserModel;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.anwsers.RangeAnswerModel;
import fr.tobby.tripnjoyback.repository.AnswersRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchMaker {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final AnswersRepository answersRepository;
    private final MatchMakerScoreComputer scoreComputer;
    private final GroupService groupService;
    private final GroupRepository groupRepository;

    public MatchMaker(final ProfileRepository profileRepository, final UserRepository userRepository,
                      final AnswersRepository answersRepository, final MatchMakerScoreComputer scoreComputer,
                      final GroupService groupService, final GroupRepository groupRepository)
    {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.answersRepository = answersRepository;
        this.scoreComputer = scoreComputer;
        this.groupService = groupService;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void match(@NotNull final MatchMakingUserModel user)
    {
        Collection<GroupEntity> groups = groupRepository.findAvailableGroups();
        Optional<GroupEntity> matchedGroup = groups.stream()
                                                   .map(group -> {
                                                       long profileId = group.getProfile().getId();
                                                       AnswersEntity groupAnswers = answersRepository.findByProfileId(profileId);

                                                       ProfileModel profileModel = ProfileModel.of(group.getProfile(), groupAnswers);
                                                       return new Pair<>(group, profileModel);
                                                   })
                                                   .filter(pair -> scoreComputer.isUserCompatible(pair.right(), user))
                                                   .map(pair -> {
                                                       float score = scoreComputer.computeMatchingScore(user.getProfile(), pair.right());
                                                       return new Pair<>(pair.left(), score);
                                                   })
                                                   .filter(pair -> pair.right() > 0) // TODO: replace '0' by minimal matching score
                                                   .max(Comparator.comparingDouble(Pair::right))
                                                   .map(Pair::left);

        if (matchedGroup.isPresent())
        {
            GroupEntity group = matchedGroup.get();
            groupService.addUserToPublicGroup(group.getId(), user.getUserId(), user.getProfile().getId());
            return;
        }


        Collection<MatchMakingUserModel> others = userRepository.findAllByWaitingForGroupIsTrue()
                                                                .stream()
                                                                .map(other -> {
                                                                    ProfileEntity profileEntity = profileRepository.findByActiveIsTrueAndUserId(other.getId()).orElseThrow(() -> new IllegalStateException("Awaiting user should have an active profile"));
                                                                    AnswersEntity answers = answersRepository.findByProfileId(profileEntity.getId());
                                                                    ProfileModel profileModel = ProfileModel.of(profileEntity, answers);
                                                                    return MatchMakingUserModel.from(other, profileModel);
                                                                })
                                                                .collect(Collectors.toSet());


        others.stream()
              .filter(other -> scoreComputer.isUserCompatible(user.getProfile(), other) && scoreComputer.isUserCompatible(other.getProfile(), user))
              .map(other -> {
                  float score = scoreComputer.computeMatchingScore(user.getProfile(), other.getProfile());
                  return new Pair<>(other, score);
              })
              .filter(pair -> pair.right() > 0) // TODO: replace '0' by minimal matching score
              .max(Comparator.comparingDouble(Pair::right))
              .ifPresentOrElse(matched -> {
                  RangeAnswerModel sizeRange = scoreComputer.computeCommonRange(user.getProfile().getGroupeSize(), matched.left().getProfile().getGroupeSize()).orElseThrow();
                  int maxSize = (sizeRange.getMaxValue() + sizeRange.getMinValue()) / 2;

                  UserEntity userEntity = userRepository.getById(user.getUserId());
                  UserEntity matchedEntity = userRepository.getById(matched.left().getUserId());
                  groupService.createPublicGroup(userEntity,
                          profileRepository.getById(user.getProfile().getId()),
                          matchedEntity,
                          profileRepository.getById(matched.left().getProfile().getId()),
                          maxSize);

                  matchedEntity.setWaitingForGroup(false);
              }, () -> {
                  userRepository.getById(user.getUserId()).setWaitingForGroup(true);
              });
    }
}
