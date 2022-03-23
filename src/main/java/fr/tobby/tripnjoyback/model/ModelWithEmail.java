package fr.tobby.tripnjoyback.model;

import lombok.Getter;

@Getter
public abstract class ModelWithEmail {
    protected String email;

    public void formatEmail(){
        email = email.toLowerCase().trim();
    }
}
