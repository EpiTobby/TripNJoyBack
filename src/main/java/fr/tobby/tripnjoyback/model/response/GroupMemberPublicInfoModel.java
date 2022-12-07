package fr.tobby.tripnjoyback.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.tobby.tripnjoyback.model.MemberModel;

@JsonSerialize
@JsonAutoDetect
public record GroupMemberPublicInfoModel(long id, String firstname, String lastname) {

    public static GroupMemberPublicInfoModel of(MemberModel memberModel)
    {
        return new GroupMemberPublicInfoModel(memberModel.getId(), memberModel.getFirstname(), memberModel.getLastname());
    }
}
