package fr.tobby.tripnjoyback.model;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@Getter
public final class MatchMakingUserModel {

    private final long userId;
    private final Gender gender;
    private final CityModel model;
    private final int age;
    private final ProfileModel profile;

    public MatchMakingUserModel(final long userId, @NotNull final Gender gender, @NotNull final CityModel model, final int age,
                                @NotNull final ProfileModel profile)
    {
        this.userId = userId;
        this.gender = gender;
        this.model = model;
        this.age = age;
        this.profile = profile;
    }

    public static MatchMakingUserModel from(@NotNull UserModel userModel, @NotNull ProfileModel profile)
    {
        return from(userModel, profile, Instant.now());
    }

    public static MatchMakingUserModel from(@NotNull UserModel userModel, @NotNull ProfileModel profile, @NotNull Instant now)
    {
        long age = userModel.getBirthDate() == null
                   ? -1
                   : Duration.between(userModel.getBirthDate(), now).toDays() / 365;

        return new MatchMakingUserModel(userModel.getId(),
                userModel.getGender(),
                userModel.getCity(),
                (int) age,
                profile);
    }
}
