package fr.tripnjoy.groups.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public record GroupMemberModel(long userId, String firstname, String lastname, String profilePicture) {

}
