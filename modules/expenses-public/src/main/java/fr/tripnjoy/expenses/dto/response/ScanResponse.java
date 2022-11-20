package fr.tripnjoy.expenses.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@JsonAutoDetect
@Getter
@AllArgsConstructor
public class ScanResponse {

    private final Map<String, Float> items;
    private final float total;
}
