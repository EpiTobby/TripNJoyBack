package fr.tripnjoy.users.model;

import fr.tripnjoy.users.entity.GenderEntity;
import org.springframework.lang.NonNull;

public enum Gender {
    MALE,
    FEMALE,
    NOT_SPECIFIED,
    ;

    @NonNull
    public static Gender of(final GenderEntity entity)
    {
        return switch (entity.getValue())
                {
                    case "male" -> MALE;
                    case "female" -> FEMALE;
                    default -> NOT_SPECIFIED;
                };
    }
}
