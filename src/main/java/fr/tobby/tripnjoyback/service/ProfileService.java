package fr.tobby.tripnjoyback.service;
import fr.tobby.tripnjoyback.entity.AnswersEntity;
import fr.tobby.tripnjoyback.entity.AvailabiltyEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.exception.BadAvailabilityException;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.ProfileUpdateRequest;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.DestinationTypeAnswer;
import fr.tobby.tripnjoyback.repository.AnswersRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AnswersRepository answersRepository;
    private final DateFormat dateFormat;

    public ProfileService(ProfileRepository profileRepository, AnswersRepository answersRepository) {
        this.profileRepository = profileRepository;
        this.answersRepository = answersRepository;
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    @Transactional
    public ProfileModel createProfile(long userId, ProfileCreationRequest profileCreationRequest){
        ProfileEntity profileEntity = ProfileEntity.builder()
                .userId(userId)
                .active(true).build();
        setProfileInactive(userId);
        profileRepository.save(profileEntity);
        AnswersEntity answersEntity = AnswersEntity.builder()
                .profileId(profileEntity.getId())
                .availabilities(profileCreationRequest.getAvailabilities().stream().map(a -> new AvailabiltyEntity(dateFormat.format(a.getStartDate()),dateFormat.format(a.getEndDate()))).toList())
                .durationMin(profileCreationRequest.getDuration().getMinValue())
                .durationMax(profileCreationRequest.getDuration().getMaxValue())
                .budgetMin(profileCreationRequest.getBudget().getMinValue())
                .budgetMax(profileCreationRequest.getBudget().getMaxValue())
                .destinationTypes(profileCreationRequest.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList())
                .ageMin(profileCreationRequest.getAges().getMinValue())
                .ageMax(profileCreationRequest.getAges().getMaxValue())
                .travelWithPersonFromSameCity(profileCreationRequest.getTravelWithPersonFromSameCity().toBoolean())
                .travelWithPersonFromSameCountry(profileCreationRequest.getTravelWithPersonFromSameCountry().toBoolean())
                .travelWithPersonSameLanguage(profileCreationRequest.getTravelWithPersonSameLanguage().toBoolean())
                .gender(profileCreationRequest.getGender().toString())
                .groupSizeMin(profileCreationRequest.getGroupSize().getMinValue())
                .groupSizeMax(profileCreationRequest.getGroupSize().getMaxValue())
                .chillOrVisit(profileCreationRequest.getChillOrVisit().toString())
                .aboutFood(profileCreationRequest.getAboutFood().toString())
                .goOutAtNight(profileCreationRequest.getGoOutAtNight().toBoolean())
                .sport(profileCreationRequest.getSport().toBoolean())
                .build();
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
        profileEntity.ifPresent(profile -> profile.setActive(false));
    }

    @Transactional
    public void updateProfile(long userId, long profileId, ProfileUpdateRequest profileUpdateRequest) {
        ProfileEntity profileEntity = profileRepository.findByIdAndUserId(profileId, userId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        if (profileUpdateRequest.getActive() != null) {
            if (profileUpdateRequest.getActive())
                setProfileInactive(userId);
            profileEntity.setActive(profileUpdateRequest.getActive());
        }
        if (profileUpdateRequest.getName() != null)
            profileEntity.setName(profileUpdateRequest.getName());
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        if (profileUpdateRequest.getAvailabilities() != null)
            answersEntity.setAvailabilities(profileUpdateRequest.getAvailabilities().stream().map(a -> new AvailabiltyEntity(dateFormat.format(a.getStartDate()),dateFormat.format(a.getEndDate()))).toList());
        if (profileUpdateRequest.getDuration() != null) {
            answersEntity.setDurationMin(profileUpdateRequest.getDuration().getMinValue());
            answersEntity.setDurationMax(profileUpdateRequest.getDuration().getMaxValue());
        }
        if (profileUpdateRequest.getBudget() != null){
            answersEntity.setBudgetMin(profileUpdateRequest.getBudget().getMinValue());
            answersEntity.setBudgetMax(profileUpdateRequest.getBudget().getMaxValue());
        }
        if (profileUpdateRequest.getDestinationTypes() != null)
            answersEntity.setDestinationTypes(profileUpdateRequest.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList());
        if (profileUpdateRequest.getAges() != null){
            answersEntity.setAgeMin(profileUpdateRequest.getAges().getMinValue());
            answersEntity.setAgeMax(profileUpdateRequest.getAges().getMaxValue());
        }
        if (profileUpdateRequest.getTravelWithPersonFromSameCity() != null)
            answersEntity.setTravelWithPersonFromSameCity(profileUpdateRequest.getTravelWithPersonFromSameCity().toBoolean());
        if (profileUpdateRequest.getTravelWithPersonFromSameCountry() != null)
            answersEntity.setTravelWithPersonFromSameCountry(profileUpdateRequest.getTravelWithPersonFromSameCountry().toBoolean());
        if (profileUpdateRequest.getTravelWithPersonSameLanguage() != null)
            answersEntity.setTravelWithPersonSameLanguage(profileUpdateRequest.getTravelWithPersonSameLanguage().toBoolean());
        if (profileUpdateRequest.getGender() != null)
            answersEntity.setGender(profileUpdateRequest.getGender().toString());
        if (profileUpdateRequest.getGroupSize() != null) {
            answersEntity.setGroupSizeMin(profileUpdateRequest.getGroupSize().getMinValue());
            answersEntity.setGroupSizeMax(profileUpdateRequest.getGroupSize().getMaxValue());
        }
        if (profileUpdateRequest.getChillOrVisit() != null)
            answersEntity.setChillOrVisit(profileUpdateRequest.getChillOrVisit().toString());
        if (profileUpdateRequest.getAboutFood() != null)
            answersEntity.setAboutFood(profileUpdateRequest.getAboutFood().toString());
        if (profileUpdateRequest.getGoOutAtNight() != null)
            answersEntity.setGoOutAtNight(profileUpdateRequest.getGoOutAtNight().toBoolean());
        if (profileUpdateRequest.getSport() != null)
            answersEntity.setSport(profileUpdateRequest.getSport().toBoolean());
        answersRepository.save(answersEntity);
    }
}
