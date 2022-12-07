package fr.tobby.tripnjoyback.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.State;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@JsonSerialize
@JsonAutoDetect
public record GroupInfoModel(long id, @Nullable String name, Collection<GroupMemberPublicInfoModel> members, int maxSize, State state,
                             @Nullable String picture) {

    public static GroupInfoModel of(GroupModel groupModel)
    {
        List<GroupMemberPublicInfoModel> members = groupModel.getMembers()
                                                             .stream()
                                                             .map(GroupMemberPublicInfoModel::of)
                                                             .toList();
        return new GroupInfoModel(groupModel.getId(), groupModel.getName(), members, groupModel.getMaxSize(), groupModel.getState(), groupModel.getPicture());
    }
}
