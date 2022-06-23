package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Getter
@Builder
public class ExpenseRequest {
    @NotNull
    private String description;
    private double total;
    private List<MoneyDueRequest> moneyDueByEachUser;
    private boolean isEvenlyDivided;
    @Nullable
    private String icon;

    @JsonProperty("total")
    public void setTotal(double total){
        if (total < 0)
            throw new UnsupportedOperationException();
        this.total = total;
    }
}
