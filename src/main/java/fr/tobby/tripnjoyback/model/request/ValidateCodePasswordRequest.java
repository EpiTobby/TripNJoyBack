package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class ValidateCodePasswordRequest {
    private String email;
    private String value;
    private String newPassword;
}
