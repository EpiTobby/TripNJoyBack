package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@JsonAutoDetect
@NoArgsConstructor
public class UserCreationRequest {

    private String firstname;
    private String lastname;
    private String password;
    private String gender;
    @ApiModelProperty("Also known as username. Must be unique")
    private String email;
    private Date birthDate;
    private String phoneNumber;

}
