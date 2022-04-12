package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.AnswersEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.model.MatchMakingUserModel;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.repository.AnswersRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MatchMaker {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final AnswersRepository answersRepository;

    public MatchMaker(final ProfileRepository profileRepository, final UserRepository userRepository,
                      final AnswersRepository answersRepository)
    {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.answersRepository = answersRepository;
    }

    public void match(@NotNull final MatchMakingUserModel user)
    {
        // TODO: Find groups

        // TODO: Find awaiting users
        Collection<MatchMakingUserModel> others = userRepository.findAllByWaitingForGroupIsTrue()
                                                                .stream()
                                                                .map(other -> {
                                                                    ProfileEntity profileEntity = profileRepository.findByActiveIsTrueAndUserId(other.getId()).orElseThrow(() -> new IllegalStateException("Awaiting user should have an active profile"));
                                                                    AnswersEntity answers = answersRepository.findByProfileId(profileEntity.getId());
                                                                    ProfileModel profileModel = ProfileModel.of(profileEntity, answers);
                                                                    return MatchMakingUserModel.from(other, profileModel);
                                                                })
                                                                .collect(Collectors.toSet());

        // TODO: Set user as awaiting

    }
}
