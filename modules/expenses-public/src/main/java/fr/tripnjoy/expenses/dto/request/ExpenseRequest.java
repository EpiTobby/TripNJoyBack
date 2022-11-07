package fr.tripnjoy.expenses.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JsonAutoDetect
@Getter
@NoArgsConstructor
@Setter
public class ExpenseRequest {
    @NotNull
    private String description = "";
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
