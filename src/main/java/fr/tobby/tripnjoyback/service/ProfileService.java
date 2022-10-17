package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.PromStats;
import fr.tobby.tripnjoyback.entity.ProfileAnswersEntity;
import fr.tobby.tripnjoyback.entity.AvailabiltyEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotConfirmedException;
import fr.tobby.tripnjoyback.model.IProfile;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.ProfileUpdateRequest;
import fr.tobby.tripnjoyback.model.request.anwsers.DestinationTypeAnswer;
import fr.tobby.tripnjoyback.repository.ProfileAnswersRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
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
    private final ProfileAnswersRepository profileAnswersRepository;
    private final UserRepository userRepository;
    private final DateFormat dateFormat;
    private final PromStats promStats;

    public ProfileService(ProfileRepository profileRepository, ProfileAnswersRepository profileAnswersRepository, final UserRepository userRepository, final PromStats promStats) {
        this.profileRepository = profileRepository;
        this.profileAnswersRepository = profileAnswersRepository;
        this.userRepository = userRepository;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.promStats = promStats;
    }

    @Transactional
    public ProfileModel createUserProfile(long userId, ProfileCreationRequest profileCreationRequest)
    {
        setProfileInactive(userId);
        ProfileModel createdProfileModel = createProfile(profileCreationRequest);
        userRepository.findById(userId).orElseThrow(UserNotConfirmedException::new)
                      .getProfiles().add(profileRepository.getById(createdProfileModel.getId()));
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
        promStats.getProfileCount().set(profileRepository.count());
        ProfileAnswersEntity profileAnswersEntity = createAnswersEntity(profileCreationRequest, profileEntity.getId());
        return ProfileModel.of(profileEntity, profileAnswersEntity);
    }

    ProfileAnswersEntity createAnswersEntity(final IProfile profile, final long profileId)
    {
        ProfileAnswersEntity profileAnswersEntity = ProfileAnswersEntity.builder()
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
        return profileAnswersRepository.save(profileAnswersEntity);
    }

    ProfileEntity createProfile(final ProfileModel model)
    {
        ProfileEntity profileEntity = profileRepository.save(ProfileEntity.builder()
                                                                          .name(model.getName())
                                                                          .active(true)
                                                                          .createdDate(Instant.now())
                                                                          .build());
        promStats.getProfileCount().set(profileRepository.count());
        createAnswersEntity(model, profileEntity.getId());
        return profileEntity;
    }

    public ProfileModel getProfile(long profileId)
    {
        return getProfile(profileRepository.findById(profileId).orElseThrow(ProfileNotFoundException::new));
    }

    ProfileModel getProfile(ProfileEntity entity)
    {
        ProfileAnswersEntity profileAnswersEntity = profileAnswersRepository.findByProfileId(entity.getId());
        return ProfileModel.of(entity, profileAnswersEntity);
    }

    public List<ProfileModel> getUserProfiles(long userId)
    {
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        return profileEntities.stream().map(e -> ProfileModel.of(e, profileAnswersRepository.findByProfileId(e.getId()))).toList();
    }

    @Transactional
    public void deleteProfilesByUserId(long userId)
    {
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(userId);
        for (ProfileEntity profileEntity : profileEntities)
        {
            ProfileAnswersEntity profileAnswersEntity = profileAnswersRepository.findByProfileId(profileEntity.getId());
            profileAnswersRepository.deleteByProfileId(profileAnswersEntity.getProfileId());
        }
    }

    public List<ProfileModel> getActiveProfiles()
    {
        List<ProfileEntity> profileEntities = profileRepository.findByActiveIsTrue();
        return profileEntities.stream().map(e -> ProfileModel.of(e, profileAnswersRepository.findByProfileId(e.getId()))).toList();
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
        return userRepository.findById(userId)
                             .flatMap(userEntity -> userEntity.getProfiles()
                                                              .stream()
                                                              .filter(ProfileEntity::isActive)
                                                              .findAny()
                             );
    }

    @Transactional
    public void deleteProfile(long userId, long profileId)
    {
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with this id"));
        if (profileEntity.isActive())
            throw new IllegalArgumentException("Cannot delete an active profile");
        userRepository.getById(userId).getProfiles().remove(profileEntity);
        profileRepository.delete(profileEntity);
        promStats.getProfileCount().set(profileRepository.count());
        ProfileAnswersEntity profileAnswersEntity = profileAnswersRepository.findByProfileId(profileId);
        profileAnswersRepository.deleteByProfileId(profileAnswersEntity.getProfileId());
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
        ProfileAnswersEntity profileAnswersEntity = profileAnswersRepository.findByProfileId(profileId);
        if (profileUpdateRequest.getAvailabilities() != null && !profileUpdateRequest.getAvailabilities().isEmpty())
        {
            profileAnswersEntity.setAvailabilities(profileUpdateRequest.getAvailabilities().stream().map(a -> new AvailabiltyEntity(dateFormat.format(a.getStartDate()), dateFormat.format(a.getEndDate()))).toList());
        }
        if (profileUpdateRequest.getDuration() != null)
        {
            profileAnswersEntity.setDurationMin(profileUpdateRequest.getDuration().getMinValue());
            profileAnswersEntity.setDurationMax(profileUpdateRequest.getDuration().getMaxValue());
        }
        if (profileUpdateRequest.getBudget() != null)
        {
            profileAnswersEntity.setBudgetMin(profileUpdateRequest.getBudget().getMinValue());
            profileAnswersEntity.setBudgetMax(profileUpdateRequest.getBudget().getMaxValue());
        }
        if (profileUpdateRequest.getDestinationTypes() != null && !profileUpdateRequest.getDestinationTypes().isEmpty())
        {
            profileAnswersEntity.setDestinationTypes(profileUpdateRequest.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList());
        }
        if (profileUpdateRequest.getAges() != null)
        {
            profileAnswersEntity.setAgeMin(profileUpdateRequest.getAges().getMinValue());
            profileAnswersEntity.setAgeMax(profileUpdateRequest.getAges().getMaxValue());
        }
        if (profileUpdateRequest.getTravelWithPersonFromSameCity() != null)
        {
            profileAnswersEntity.setTravelWithPersonFromSameCity(profileUpdateRequest.getTravelWithPersonFromSameCity().toBoolean());
        }
        if (profileUpdateRequest.getTravelWithPersonFromSameCountry() != null)
        {
            profileAnswersEntity.setTravelWithPersonFromSameCountry(profileUpdateRequest.getTravelWithPersonFromSameCountry().toBoolean());
        }
        if (profileUpdateRequest.getTravelWithPersonSameLanguage() != null)
        {
            profileAnswersEntity.setTravelWithPersonSameLanguage(profileUpdateRequest.getTravelWithPersonSameLanguage().toBoolean());
        }
        if (profileUpdateRequest.getGender() != null) {
            profileAnswersEntity.setGender(profileUpdateRequest.getGender().toString());
        }
        if (profileUpdateRequest.getGroupSize() != null) {
            profileAnswersEntity.setGroupSizeMin(profileUpdateRequest.getGroupSize().getMinValue());
            profileAnswersEntity.setGroupSizeMax(profileUpdateRequest.getGroupSize().getMaxValue());
        }
        if (profileUpdateRequest.getChillOrVisit() != null) {
            profileAnswersEntity.setChillOrVisit(profileUpdateRequest.getChillOrVisit().toString());
        }
        if (profileUpdateRequest.getAboutFood() != null) {
            profileAnswersEntity.setAboutFood(profileUpdateRequest.getAboutFood().toString());
        }
        if (profileUpdateRequest.getGoOutAtNight() != null) {
            profileAnswersEntity.setGoOutAtNight(profileUpdateRequest.getGoOutAtNight().toBoolean());
        }
        if (profileUpdateRequest.getSport() != null) {
            profileAnswersEntity.setSport(profileUpdateRequest.getSport().toBoolean());
        }
        profileAnswersRepository.save(profileAnswersEntity);
    }
}
