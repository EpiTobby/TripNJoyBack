package fr.tobby.tripnjoyback.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class UserCreationModel {

    private String firstname;
    private String lastname;
    private String password;
    private String gender;
    private String email;
    private Instant birthDate;

    public UserCreationModel()
    {

    }
}
