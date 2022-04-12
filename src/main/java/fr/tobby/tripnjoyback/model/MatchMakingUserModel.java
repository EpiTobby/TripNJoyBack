package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@Getter
public final class MatchMakingUserModel {

    private final long userId;
    private final Gender gender;
    private final CityEntity model;
    private final int age;
    private final ProfileModel profile;

    public MatchMakingUserModel(final long userId, @NotNull final Gender gender, @NotNull final CityEntity model, final int age,
                                @NotNull final ProfileModel profile)
    {
        this.userId = userId;
        this.gender = gender;
        this.model = model;
        this.age = age;
        this.profile = profile;
    }

    public static MatchMakingUserModel from(@NotNull UserEntity userEntity, @NotNull ProfileModel profile)
    {
        return from(userEntity, profile, Instant.now());
    }

    public static MatchMakingUserModel from(@NotNull UserEntity userEntity, @NotNull ProfileModel profile, @NotNull Instant now)
    {
        long age = userEntity.getBirthDate() == null
                   ? -1
                   : Duration.between(userEntity.getBirthDate(), now).toDays() / 365;

        return new MatchMakingUserModel(userEntity.getId(),
                Gender.of(userEntity.getGender()),
                userEntity.getCity(),
                (int) age,
                profile);
    }
}
