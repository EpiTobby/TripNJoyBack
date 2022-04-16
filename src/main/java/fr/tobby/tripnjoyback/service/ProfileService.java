package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.AnswersEntity;
import fr.tobby.tripnjoyback.entity.AvailabiltyEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotConfirmedException;
import fr.tobby.tripnjoyback.model.IProfile;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.ProfileUpdateRequest;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.DestinationTypeAnswer;
import fr.tobby.tripnjoyback.repository.AnswersRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService extends IdCheckerService {
    private final ProfileRepository profileRepository;
    private final AnswersRepository answersRepository;
    private final UserRepository userRepository;
    private final DateFormat dateFormat;

    public ProfileService(ProfileRepository profileRepository, AnswersRepository answersRepository, UserRepository userRepository) {
        super(userRepository);
        this.profileRepository = profileRepository;
        this.answersRepository = answersRepository;
        this.userRepository = userRepository;
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    @Transactional
    public ProfileModel createProfile(long userId, ProfileCreationRequest profileCreationRequest)
    {
        ProfileEntity profileEntity = ProfileEntity.builder()
                                                   .name(profileCreationRequest.getName())
                                                   .active(true).build();
        userRepository.findById(userId).orElseThrow(UserNotConfirmedException::new)
                      .getProfiles().add(profileEntity);
        setProfileInactive(userId);
        profileRepository.save(profileEntity);
        AnswersEntity answersEntity = createAnswersEntity(profileCreationRequest, profileEntity.getId());
        return ProfileModel.of(profileEntity, answersEntity);
    }

    AnswersEntity createAnswersEntity(final IProfile profile, final long profileId)
    {
        AnswersEntity answersEntity = AnswersEntity.builder()
                                                   .profileId(profileId)
                                                   .availabilities(profile.getAvailabilities().stream().map(a -> new AvailabiltyEntity(a.getStartDate(), a.getEndDate())).toList())
                                                   .durationMin(profile.getDuration().getMinValue())
                                                   .durationMax(profile.getDuration().getMaxValue())
                                                   .budgetMin(profile.getBudget().getMinValue())
                                                   .budgetMax(profile.getBudget().getMaxValue())
                                                   .destinationTypes(profile.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList())
                                                   .ageMin(profile.getAges().getMinValue())
                                                   .ageMax(profile.getAges().getMaxValue())
                                                   .travelWithPersonFromSameCity(profile.getTravelWithPersonFromSameCity().toBoolean())
                                                   .travelWithPersonFromSameCountry(profile.getTravelWithPersonFromSameCountry().toBoolean())
                                                   .travelWithPersonSameLanguage(profile.getTravelWithPersonSameLanguage().toBoolean())
                                                   .gender(profile.getGender().toString())
                                                   .groupSizeMin(profile.getGroupSize().getMinValue())
                                                   .groupSizeMax(profile.getGroupSize().getMaxValue())
                                                   .chillOrVisit(profile.getChillOrVisit().toString())
                                                   .aboutFood(profile.getAboutFood().toString())
                                                   .goOutAtNight(profile.getGoOutAtNight().toBoolean())
                                                   .sport(profile.getSport().toBoolean())
                                                   .build();
        return answersRepository.save(answersEntity);
    }

    ProfileEntity createProfile(final ProfileModel model)
    {
        ProfileEntity profileEntity = profileRepository.save(ProfileEntity.builder()
                                                                          .name(model.getName())
                                                                          .active(true)
                                                                          .build());
        createAnswersEntity(model, profileEntity.getId());
        return profileEntity;
    }

    public ProfileModel getProfile(long profileId)
    {
        return getProfile(profileRepository.findById(profileId).orElseThrow(ProfileNotFoundException::new));
    }

    ProfileModel getProfile(ProfileEntity entity)
    {
        AnswersEntity answersEntity = answersRepository.findByProfileId(entity.getId());
        return ProfileModel.of(entity, answersEntity);
    }

    public List<ProfileModel> getUserProfiles(long userId)
    {
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        return profileEntities.stream().map(e -> ProfileModel.of(e, answersRepository.findByProfileId(e.getId()))).toList();
    }

    @Transactional
    public void deleteProfilesByUserId(long userId)
    {
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        for (ProfileEntity profileEntity : profileEntities)
        {
            AnswersEntity answersEntity = answersRepository.findByProfileId(profileEntity.getId());
            answersRepository.deleteByProfileId(answersEntity.getProfileId());
        }
    }

    public List<ProfileModel> getActiveProfiles()
    {
        List<ProfileEntity> profileEntities = profileRepository.findByActiveIsTrue();
        return profileEntities.stream().map(e -> ProfileModel.of(e, answersRepository.findByProfileId(e.getId()))).toList();
    }

    public Optional<ProfileModel> getActiveProfile(long userId)
    {
        return profileRepository.findByActiveIsTrueAndUserId(userId)
                                .map(this::getProfile);
    }

    @Transactional
    public void deleteProfile(long userId, long profileId)
    {
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        profileRepository.delete(profileEntity);
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        answersRepository.deleteByProfileId(answersEntity.getProfileId());
    }

    @Transactional
    public void setProfileInactive(long userId) {
        Optional<ProfileEntity> profileEntity = profileRepository.findByActiveIsTrueAndUserId(userId);
        profileEntity.ifPresent(profile -> profile.setActive(false));
    }

    @Transactional
    public void updateProfile(long userId, long profileId, ProfileUpdateRequest profileUpdateRequest) {
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        if (profileUpdateRequest.getActive() != null)
        {
            if (Boolean.TRUE.equals(profileUpdateRequest.getActive()))
                setProfileInactive(userId);
            else
                profileEntity.setActive(profileUpdateRequest.getActive());
        }
        if (profileUpdateRequest.getName() != null)
        {
            profileEntity.setName(profileUpdateRequest.getName());
        }
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        if (profileUpdateRequest.getAvailabilities() != null && !profileUpdateRequest.getAvailabilities().isEmpty())
        {
            answersEntity.setAvailabilities(profileUpdateRequest.getAvailabilities().stream().map(a -> new AvailabiltyEntity(a.getStartDate(), a.getEndDate())).toList());
        }
        if (profileUpdateRequest.getDuration() != null)
        {
            answersEntity.setDurationMin(profileUpdateRequest.getDuration().getMinValue());
            answersEntity.setDurationMax(profileUpdateRequest.getDuration().getMaxValue());
        }
        if (profileUpdateRequest.getBudget() != null)
        {
            answersEntity.setBudgetMin(profileUpdateRequest.getBudget().getMinValue());
            answersEntity.setBudgetMax(profileUpdateRequest.getBudget().getMaxValue());
        }
        if (profileUpdateRequest.getDestinationTypes() != null && !profileUpdateRequest.getDestinationTypes().isEmpty())
        {
            answersEntity.setDestinationTypes(profileUpdateRequest.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList());
        }
        if (profileUpdateRequest.getAges() != null)
        {
            answersEntity.setAgeMin(profileUpdateRequest.getAges().getMinValue());
            answersEntity.setAgeMax(profileUpdateRequest.getAges().getMaxValue());
        }
        if (profileUpdateRequest.getTravelWithPersonFromSameCity() != null)
        {
            answersEntity.setTravelWithPersonFromSameCity(profileUpdateRequest.getTravelWithPersonFromSameCity().toBoolean());
        }
        if (profileUpdateRequest.getTravelWithPersonFromSameCountry() != null)
        {
            answersEntity.setTravelWithPersonFromSameCountry(profileUpdateRequest.getTravelWithPersonFromSameCountry().toBoolean());
        }
        if (profileUpdateRequest.getTravelWithPersonSameLanguage() != null)
        {
            answersEntity.setTravelWithPersonSameLanguage(profileUpdateRequest.getTravelWithPersonSameLanguage().toBoolean());
        }
        if (profileUpdateRequest.getGender() != null) {
            answersEntity.setGender(profileUpdateRequest.getGender().toString());
        }
        if (profileUpdateRequest.getGroupSize() != null) {
            answersEntity.setGroupSizeMin(profileUpdateRequest.getGroupSize().getMinValue());
            answersEntity.setGroupSizeMax(profileUpdateRequest.getGroupSize().getMaxValue());
        }
        if (profileUpdateRequest.getChillOrVisit() != null) {
            answersEntity.setChillOrVisit(profileUpdateRequest.getChillOrVisit().toString());
        }
        if (profileUpdateRequest.getAboutFood() != null) {
            answersEntity.setAboutFood(profileUpdateRequest.getAboutFood().toString());
        }
        if (profileUpdateRequest.getGoOutAtNight() != null) {
            answersEntity.setGoOutAtNight(profileUpdateRequest.getGoOutAtNight().toBoolean());
        }
        if (profileUpdateRequest.getSport() != null) {
            answersEntity.setSport(profileUpdateRequest.getSport().toBoolean());
        }
        answersRepository.save(answersEntity);
    }
}
