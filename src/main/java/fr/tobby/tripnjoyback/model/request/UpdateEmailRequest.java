package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class UpdateEmailRequest {
    private String newEmail;
    private String password;
}
