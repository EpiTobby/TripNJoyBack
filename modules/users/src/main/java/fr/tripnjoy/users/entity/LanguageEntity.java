package fr.tripnjoy.users.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "languages")
@NoArgsConstructor
@Getter
public class LanguageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    public LanguageEntity(String value) {
        this.id = null;
        this.value = value;
    }
}