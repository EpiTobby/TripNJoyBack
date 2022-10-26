package fr.tripnjoy.profiles.service;

import fr.tripnjoy.profiles.entity.AnswersEntity;
import fr.tripnjoy.profiles.entity.AvailabiltyEntity;
import fr.tripnjoy.profiles.entity.ProfileEntity;
import fr.tripnjoy.profiles.entity.UserProfileEntity;
import fr.tripnjoy.profiles.exception.ProfileNotFoundException;
import fr.tripnjoy.profiles.model.IProfile;
import fr.tripnjoy.profiles.model.ProfileModel;
import fr.tripnjoy.profiles.model.answer.DestinationTypeAnswer;
import fr.tripnjoy.profiles.model.request.ProfileCreationRequest;
import fr.tripnjoy.profiles.model.request.ProfileUpdateRequest;
import fr.tripnjoy.profiles.repository.AnswersRepository;
import fr.tripnjoy.profiles.repository.ProfileRepository;
import fr.tripnjoy.profiles.repository.UserProfileRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AnswersRepository answersRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserFeignClient userFeignClient;
    private final DateFormat dateFormat;

    public ProfileService(ProfileRepository profileRepository, AnswersRepository answersRepository,
                          final UserProfileRepository userProfileRepository, final UserFeignClient userFeignClient) {
        this.profileRepository = profileRepository;
        this.answersRepository = answersRepository;
        this.userProfileRepository = userProfileRepository;
        this.userFeignClient = userFeignClient;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Transactional
    public ProfileModel createUserProfile(long userId, ProfileCreationRequest profileCreationRequest)
    {
        setProfileInactive(userId);
        ProfileModel createdProfileModel = createProfile(profileCreationRequest);
        if (!userFeignClient.exists(userId).value())
            throw new UserNotFoundException();
        userProfileRepository.save(new UserProfileEntity(userId, profileRepository.getById(createdProfileModel.getId())));
        return createdProfileModel;
    }

    @Transactional
    public ProfileModel createProfile(ProfileCreationRequest profileCreationRequest)
    {
        ProfileEntity profileEntity = ProfileEntity.builder()
                                                   .name(profileCreationRequest.getName())
                                                   .active(true)
                                                   .createdDate(Instant.now())
                                                   .build();
        profileEntity = profileRepository.save(profileEntity);
        // FIXME: prom
//        promStats.getProfileCount().set(profileRepository.count());
        AnswersEntity answersEntity = createAnswersEntity(profileCreationRequest, profileEntity.getId());
        return ProfileModel.of(profileEntity, answersEntity);
    }

    AnswersEntity createAnswersEntity(final IProfile profile, final long profileId)
    {
        AnswersEntity answersEntity = AnswersEntity.builder()
                                                   .profileId(profileId)
                                                   .availabilities(profile.getAvailabilities().stream().map(a -> new AvailabiltyEntity(dateFormat.format(a.getStartDate()), dateFormat.format(a.getEndDate()))).toList())
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
                                                                          .createdDate(Instant.now())
                                                                          .build());
        // FIXME: prom
//        promStats.getProfileCount().set(profileRepository.count());
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

    @Transactional
    void setActiveProfile(long profileId, boolean active)
    {
        profileRepository.getById(profileId).setActive(active);
    }

    Optional<ProfileModel> getActiveProfileModel(long userId)
    {
        return getActiveProfile(userId).map(this::getProfile);
    }

    public Optional<ProfileEntity> getActiveProfile(long userId)
    {
        return profileRepository.findByUserId(userId)
                                .stream()
                                .filter(ProfileEntity::isActive)
                                .findAny();
    }

    @Transactional
    public void deleteProfile(long userId, long profileId)
    {
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        if (profileEntity.isActive())
            throw new IllegalArgumentException("Cannot delete an active profile");
        profileRepository.delete(profileEntity);
        // FIXME: prom
//        promStats.getProfileCount().set(profileRepository.count());
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        answersRepository.deleteByProfileId(answersEntity.getProfileId());
    }

    @Transactional
    public void setProfileInactive(long userId)
    {
        this.getActiveProfile(userId)
            .ifPresent(profile -> profile.setActive(false));
    }

    @Transactional
    public void updateProfile(long userId, long profileId, ProfileUpdateRequest profileUpdateRequest) {
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        if (profileUpdateRequest.getActive() != null)
        {
            if (Boolean.TRUE.equals(profileUpdateRequest.getActive()))
                setProfileInactive(userId);
            profileEntity.setActive(profileUpdateRequest.getActive());
        }
        if (profileUpdateRequest.getName() != null)
        {
            profileEntity.setName(profileUpdateRequest.getName());
        }
        AnswersEntity answersEntity = answersRepository.findByProfileId(profileId);
        if (profileUpdateRequest.getAvailabilities() != null && !profileUpdateRequest.getAvailabilities().isEmpty())
        {
            answersEntity.setAvailabilities(profileUpdateRequest.getAvailabilities().stream().map(a -> new AvailabiltyEntity(dateFormat.format(a.getStartDate()), dateFormat.format(a.getEndDate()))).toList());
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
