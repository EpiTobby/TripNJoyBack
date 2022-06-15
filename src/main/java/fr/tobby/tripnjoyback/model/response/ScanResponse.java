package fr.tobby.tripnjoyback.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@JsonAutoDetect
@Getter
@AllArgsConstructor
public class ScanResponse {

    private final Map<String, Float> items;
}
