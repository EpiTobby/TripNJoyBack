package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Getter
@Builder
public class CreateExpenseRequest {
    private String description;
    private double total;
    private List<Long> userIds;

    @JsonProperty("total")
    public void setTotal(double total){
        if (total < 0)
            throw new UnsupportedOperationException();
        this.total = total;
    }
}
