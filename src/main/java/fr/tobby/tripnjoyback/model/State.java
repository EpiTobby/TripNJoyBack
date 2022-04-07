package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.entity.StateEntity;
import org.springframework.lang.NonNull;

public enum State {
    OPEN,
    CLOSE,
    ARCHIVED;

    @NonNull
    public static State of(final StateEntity entity)
    {
        return switch (entity.getValue())
                {
                    case "open" -> OPEN;
                    case "close" -> CLOSE;
                    default -> ARCHIVED;
                };
    }
}
