package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ScanRequest {
    private final String minioUrl;

    public ScanRequest(@JsonProperty("minioUrl") final String minioUrl)
    {
        this.minioUrl = minioUrl;
    }
}
