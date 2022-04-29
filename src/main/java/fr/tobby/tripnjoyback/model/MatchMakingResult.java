package fr.tobby.tripnjoyback.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatchMakingResult {

    public enum Type {
        /**
         * A new group has been created
         */
        CREATED,
        /**
         * The user has joined an existing group
         */
        JOINED,
        /**
         * No group or other user found. The user is waiting for someone else to match
         */
        WAITING,
        /**
         * Match making in progress
         */
        SEARCHING,
    }

    @NotNull
    private final Type type;
    @Nullable
    private final Long groupId;

    public MatchMakingResult(@NotNull final Type type, @Nullable final Long groupId)
    {
        if ((type != Type.WAITING && type != Type.SEARCHING) && groupId == null)
            throw new IllegalArgumentException("Group cannot be null for type " + type);
        this.type = type;
        this.groupId = groupId;
    }

    @NotNull
    public Type getType()
    {
        return type;
    }

    @Nullable
    public Long getGroupId()
    {
        return groupId;
    }
}
