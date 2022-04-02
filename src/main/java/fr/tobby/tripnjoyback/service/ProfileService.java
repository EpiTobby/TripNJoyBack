package fr.tobby.tripnjoyback.service;
import fr.tobby.tripnjoyback.entity.AnswersEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.exception.BadAvailabilityException;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.ProfileUpdateRequest;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.repository.AnswersRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AnswersRepository answersRepository;

    public ProfileService(ProfileRepository profileRepository, AnswersRepository answersRepository) {
        this.profileRepository = profileRepository;
        this.answersRepository = answersRepository;
    }

    @Transactional
    public ProfileModel createProfile(long userId, ProfileCreationRequest profilecreationRequest){
        if (profilecreationRequest.getAvailability().getStartDate().after(profilecreationRequest.getAvailability().getEndDate()))
            throw new BadAvailabilityException("Start Date must be before end Date");
        ProfileEntity profileEntity = new ProfileEntity().builder()
                .userId(userId)
                .active(true).build();
        profileRepository.save(profileEntity);
        setProfileInactive(userId);
        AnswersEntity answersEntity = new AnswersEntity(profileEntity.getId(), profilecreationRequest);
        answersRepository.save(answersEntity);
        return ProfileModel.of(profileEntity, answersEntity);
    }

    public List<ProfileModel> getUserProfiles(long userId){
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        return profileEntities.stream().map(e -> ProfileModel.of(e,answersRepository.findByProfileId(e.getId()))).toList();
    }

    @Transactional
    public void deleteProfilesByUserId(long userId){
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        for (ProfileEntity profileEntity : profileEntities) {
            AnswersEntity answersEntity = answersRepository.findByProfileId(profileEntity.getId());
            answersRepository.deleteByProfileId(answersEntity.getProfileId());
        }
    }

    public List<ProfileModel> getActiveProfiles(){
        List<ProfileEntity> profileEntities = profileRepository.findByActiveIsTrue();
        return profileEntities.stream().map(e -> ProfileModel.of(e, answersRepository.findByProfileId(e.getId()))).toList();
    }

    @Transactional
    public void deleteProfile(long userId, long profileId) {
        ProfileEntity profileEntity = profileRepository.findByIdAndUserId(profileId, userId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        profileRepository.delete(profileEntity);
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        answersRepository.deleteByProfileId(answersEntity.getProfileId());
    }

    @Transactional
    public void setProfileInactive(long userId){
        Optional<ProfileEntity> profileEntity = profileRepository.findByActiveIsTrueAndUserId(userId);
        if (profileEntity.isPresent()){
            profileEntity.get().setActive(false);
        }
    }

    @Transactional
    public void reuseProfile(long userId, long profileId, AvailabilityAnswerModel availability) {
        if (availability.getStartDate().after(availability.getEndDate()))
            throw new BadAvailabilityException("Start Date must be before end Date");
        ProfileEntity profileEntity = profileRepository.findByIdAndUserId(profileId, userId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        answersEntity.setAvailability(availability);
        if (!profileEntity.isActive()) {
            setProfileInactive(userId);
            profileEntity.setActive(true);
        }
    }

    @Transactional
    public void updateProfile(long userId, long profileId, ProfileUpdateRequest profileUpdateRequest) {
        ProfileEntity profileEntity = profileRepository.findByIdAndUserId(profileId, userId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        if (profileUpdateRequest.isActive())
            setProfileInactive(userId);
        profileEntity.setActive(profileUpdateRequest.isActive());
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        answersEntity.update(profileUpdateRequest);
    }
}
