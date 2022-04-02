package fr.tobby.tripnjoyback.service;
import fr.tobby.tripnjoyback.entity.AnswersEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationModel;
import fr.tobby.tripnjoyback.repository.AnswersRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final AnswersRepository answersRepository;

    public ProfileService(ProfileRepository profileRepository, UserService userService, AnswersRepository answersRepository) {
        this.profileRepository = profileRepository;
        this.userService = userService;
        this.answersRepository = answersRepository;
    }

    @Transactional
    public ProfileModel createProfile(long userId, ProfileCreationModel profilecreationModel){
        ProfileEntity profileEntity = new ProfileEntity().builder()
                .userId(userId)
                .active(true).build();
        profileRepository.save(profileEntity);
        AnswersEntity answersEntity = new AnswersEntity(profileEntity.getId(), profilecreationModel);
        return ProfileModel.of(profileEntity, answersEntity);
    }

    public List<ProfileModel> getUserProfiles(long userId){
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        return profileEntities.stream().map(e -> ProfileModel.of(e,answersRepository.findByProfileId(e.getId()))).toList();
    }

    public List<ProfileModel> getActiveProfiles(){
        List<ProfileEntity> profileEntities = profileRepository.findByActiveIsTrue();
        return profileEntities.stream().map(e -> ProfileModel.of(e, answersRepository.findByProfileId(e.getId()))).toList();
    }

    @Transactional
    public void deleteProfile(long userId, long profileId) {
        ProfileEntity profileEntity = profileRepository.findByIdAndUserId(profileId, userId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        profileRepository.delete(profileEntity);
    }
}
