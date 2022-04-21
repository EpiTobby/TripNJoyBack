package fr.tobby.tripnjoyback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class GroupProfileModel extends ProfileModel {

    @Getter
    @Setter
    private long groupId;
}
