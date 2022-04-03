package fr.tobby.tripnjoyback.service;
import fr.tobby.tripnjoyback.entity.AnswersEntity;
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

    public ProfileService(ProfileRepository profileRepository, AnswersRepository answersRepository) {
        this.profileRepository = profileRepository;
        this.answersRepository = answersRepository;
    }

    @Transactional
    public ProfileModel createProfile(long userId, ProfileCreationRequest profileCreationRequest){
        if (profileCreationRequest.getAvailability().getStartDate().after(profileCreationRequest.getAvailability().getEndDate()))
            throw new BadAvailabilityException("Start Date must be before end Date");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ProfileEntity profileEntity = new ProfileEntity().builder()
                .userId(userId)
                .active(true).build();
        setProfileInactive(userId);
        profileRepository.save(profileEntity);
        AnswersEntity answersEntity = AnswersEntity.builder()
                .profileId(profileEntity.getId())
                .startDate(dateFormat.format(profileCreationRequest.getAvailability().getStartDate()))
                .endDate(dateFormat.format(profileCreationRequest.getAvailability().getEndDate()))
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
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        answersEntity.setStartDate(dateFormat.format(availability.getStartDate()));
        answersEntity.setEndDate(dateFormat.format(availability.getEndDate()));
        if (!profileEntity.isActive()) {
            setProfileInactive(userId);
            profileEntity.setActive(true);
        }
        answersRepository.save(answersEntity);
    }

    @Transactional
    public void updateProfile(long userId, long profileId, ProfileUpdateRequest profileUpdateRequest) {
        ProfileEntity profileEntity = profileRepository.findByIdAndUserId(profileId, userId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        if (profileUpdateRequest.isActive())
            setProfileInactive(userId);
        profileEntity.setActive(profileUpdateRequest.isActive());
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        if (profileUpdateRequest.getAvailability() != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            answersEntity.setStartDate(dateFormat.format(profileUpdateRequest.getAvailability().getStartDate()));
            answersEntity.setEndDate(dateFormat.format(profileUpdateRequest.getAvailability().getEndDate()));
        }
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
