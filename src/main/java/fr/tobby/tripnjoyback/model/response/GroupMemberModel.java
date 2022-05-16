package fr.tobby.tripnjoyback.model.response;

import fr.tobby.tripnjoyback.entity.UserEntity;

public record GroupMemberModel(long userId, String firstname, String lastname, String profilePicture) {

    public static GroupMemberModel of(UserEntity userEntity)
    {
        return new GroupMemberModel(userEntity.getId(), userEntity.getFirstname(), userEntity.getLastname(), userEntity.getProfilePicture());
    }
}
