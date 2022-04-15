package fr.tobby.tripnjoyback.model.request.anwsers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonAutoDetect
public class RangeAnswerModel implements AnswerModel {
    private final int minValue;
    private final int maxValue;

    public RangeAnswerModel(@JsonProperty("minValue") final int minValue, @JsonProperty("maxValue") final int maxValue)
    {
        if (minValue <= 0 || maxValue <= 0 || (maxValue != 0 && minValue > maxValue))
        {
            throw new IllegalArgumentException("Cannot Serialize range Answer");
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @JsonIgnore
    public boolean isInRange(final int val)
    {
        return minValue <= val && val <= maxValue;
    }
}
