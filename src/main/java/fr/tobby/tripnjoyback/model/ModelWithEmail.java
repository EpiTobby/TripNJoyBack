package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ModelWithEmail {
    protected String email;

    @JsonProperty("email")
    public void setEmail(String email){
        this.email = email.toLowerCase().trim();
    }
}
