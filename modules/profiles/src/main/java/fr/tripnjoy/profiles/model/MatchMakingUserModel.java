package fr.tripnjoy.profiles.model;

import fr.tripnjoy.users.api.model.Gender;
import fr.tripnjoy.users.api.response.UserResponse;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@Getter
public final class MatchMakingUserModel {

    private final long userId;
    private final Gender gender;
    private final String city;
    private final int age;
    private final ProfileModel profile;

    public MatchMakingUserModel(final long userId, @NotNull final Gender gender, @NotNull final String city, final int age,
                                @NotNull final ProfileModel profile)
    {
        this.userId = userId;
        this.gender = gender;
        this.city = city;
        this.age = age;
        this.profile = profile;
    }

    public static MatchMakingUserModel from(final UserResponse user, final Instant now, @NotNull final ProfileModel profile)
    {
        int age = user.getBirthDate() == null
                  ? -1
                  : (int) (Duration.between(user.getBirthDate(), now).toDays() / 365);
        return new MatchMakingUserModel(user.getId(), user.getGender(), user.getCity(), age, profile);
    }
}
