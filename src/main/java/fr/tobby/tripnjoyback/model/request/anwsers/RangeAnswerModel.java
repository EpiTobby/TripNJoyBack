package fr.tobby.tripnjoyback.model.request.anwsers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.SerializationException;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class RangeAnswerModel implements AnswerModel {
    private int minValue;
    private int maxValue;

    @JsonProperty("minValue")
    public void setMinValue(int minValue){
        if (minValue <= 0 || (maxValue != 0 && minValue > maxValue)){
            throw new IllegalArgumentException("Cannot Serialize range Answer");
        }
        this.minValue = minValue;
    }

    @JsonProperty("maxValue")
    public void setMaxValue(int maxValue){
        if (maxValue <= 0 || (minValue != 0 & minValue > maxValue)){
            throw new IllegalArgumentException("Cannot Serialize Answer");
        }
        this.maxValue = maxValue;
    }
}
