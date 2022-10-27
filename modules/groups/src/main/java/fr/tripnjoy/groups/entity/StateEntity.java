package fr.tripnjoy.groups.entity;

import fr.tripnjoy.groups.SpringContext;
import fr.tripnjoy.groups.model.State;
import fr.tripnjoy.groups.repository.StateRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "states", uniqueConstraints = @UniqueConstraint(name = "states_value_uindex", columnNames = "value"))
@Getter
@NoArgsConstructor
public class StateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    public StateEntity(String value)
    {
        id = null;
        this.value = value;
    }

    public State toModel()
    {
        return State.valueOf(this.value);
    }

    public static StateEntity ofModel(State state)
    {
        return SpringContext.getBean(StateRepository.class)
                            .findByValue(state.toString())
                            .orElseThrow(() -> new IllegalStateException("State value " + state + " not present in database"));
    }
}
