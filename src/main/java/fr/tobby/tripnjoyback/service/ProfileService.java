package fr.tobby.tripnjoyback.service;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;

    public ProfileService(ProfileRepository profileRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userService = userService;
    }

    public List<ProfileModel> findByUserId(long userId){
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        return profileEntities.stream().map(e -> ProfileModel.of(e)).toList();
    }

    @Transactional
    public void deleteById(long profileId){
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        profileRepository.delete(profileEntity);
    }
}
