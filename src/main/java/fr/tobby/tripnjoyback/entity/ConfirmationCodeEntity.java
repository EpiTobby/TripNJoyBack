package fr.tobby.tripnjoyback.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name  = "confimation_codes")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    private String value;

    private Instant expirationDate;

    public ConfirmationCodeEntity(long userId){
        this.id = null;
        this.userId = userId;
        int code = (int) (Math.random() * (999999 - 100000) + 100000);
        this.value = String.valueOf(code);
        this.expirationDate = Instant.now().plus(24, ChronoUnit.HOURS);
    }
}
