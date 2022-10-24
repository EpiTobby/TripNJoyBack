package fr.tripnjoy.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BooleanResponse {
    private final boolean value;

    public BooleanResponse(@JsonProperty("value") final boolean value)
    {
        this.value = value;
    }

    @JsonProperty("value")
    public boolean value()
    {
        return value;
    }
}
