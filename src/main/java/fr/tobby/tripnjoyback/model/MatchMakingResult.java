package fr.tobby.tripnjoyback.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatchMakingResult {

    public enum Type {
        CREATED,
        JOINED,
        WAITING,
        SEARCHING,
    }

    @NotNull
    private final Type type;
    @Nullable
    private final Long groupId;

    public MatchMakingResult(@NotNull final Type type, @Nullable final Long groupId)
    {
        if (type != Type.WAITING && groupId == null)
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
