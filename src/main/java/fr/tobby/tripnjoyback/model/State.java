package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.StateEntity;
import fr.tobby.tripnjoyback.repository.StateRepository;
import org.apache.catalina.core.ApplicationContext;
import org.jetbrains.annotations.NotNull;

public enum State {
    OPEN,
    CLOSED,
    ARCHIVED;

    /**
     * Retrieve the entity in database associated with this value
     *
     * @return State entity
     *
     * @throws IllegalStateException If the value is not present in database
     */
    @NotNull
    public StateEntity getEntity() throws IllegalStateException
    {
        return SpringContext.getBean(StateRepository.class)
                            .findByValue(this.toString())
                            .orElseThrow(() -> new IllegalStateException("State value " + this + " not present in database"));
    }
}
