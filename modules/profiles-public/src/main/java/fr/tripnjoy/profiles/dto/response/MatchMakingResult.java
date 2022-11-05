package fr.tripnjoy.profiles.dto.response;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
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
    private final long groupId;
    private final long userId;
    private final long profileId;

    public MatchMakingResult(@NotNull final Type type, final long groupId, final long userId, final long profileId)
    {
        this.userId = userId;
        this.profileId = profileId;
        if ((type != Type.WAITING && type != Type.SEARCHING) && groupId == 0)
            throw new IllegalArgumentException("Group cannot be null for type " + type);
        this.type = type;
        this.groupId = groupId;
    }

    @NotNull
    public Type getType()
    {
        return type;
    }

    public long getGroupId()
    {
        return groupId;
    }
}
