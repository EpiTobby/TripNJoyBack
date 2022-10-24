package fr.tripnjoy.groups.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.tripnjoy.groups.model.State;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@JsonSerialize
@JsonAutoDetect
public record GroupInfoModel(long id, @Nullable String name, Collection<GroupMemberPublicInfoModel> members, int maxSize, State state,
                             @Nullable String picture) {
}
