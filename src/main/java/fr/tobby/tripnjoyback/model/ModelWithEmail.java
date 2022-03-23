package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class ModelWithEmail {
    protected String email;

    @JsonProperty("email")
    public void setEmail(String email){
        this.email = email.toLowerCase().trim();
    }
}
