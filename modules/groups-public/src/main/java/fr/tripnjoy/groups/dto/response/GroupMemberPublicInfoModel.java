package fr.tripnjoy.groups.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonAutoDetect
public record GroupMemberPublicInfoModel(long id, String firstname, String lastname) {

}
