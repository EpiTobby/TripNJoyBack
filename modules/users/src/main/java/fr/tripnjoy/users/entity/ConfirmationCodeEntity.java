package fr.tripnjoy.users.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Entity
@Table(name  = "confirmation_codes")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationCodeEntity {
    public static final Random RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    private long userId;

    private String value;

    private Instant expirationDate;

    public ConfirmationCodeEntity(long userId){
        this.id = null;
        this.userId = userId;
        int code = RANDOM.nextInt() * (999999 - 100000) + 100000;
        this.value = String.valueOf(code);
        this.expirationDate = Instant.now().plus(24, ChronoUnit.HOURS);
    }

    @Override
    public String toString()
    {
        return "ConfirmationCodeEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", value='" + value + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
