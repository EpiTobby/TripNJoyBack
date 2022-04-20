package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonSerialize
@Getter
@Setter
@NoArgsConstructor
public class GoogleTokenVerificationModel {
    private String email;
    private String aud;
}
