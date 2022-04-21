package fr.tobby.tripnjoyback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class UserProfileModel extends ProfileModel {

    @Getter
    @Setter
    private long userId;
}
