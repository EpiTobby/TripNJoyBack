package fr.tobby.tripnjoyback.model.ocr.space;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanResult {

    private final List<ParsedResult> parsedResults;
    private final int exitCode;
    private final boolean errorOnProcessing;
    private final List<String> errorMessage;
    private final List<String> errorDetails;

    public ScanResult(@JsonProperty("ParsedResults") final List<ParsedResult> parsedResults,
                      @JsonProperty("OCRExitCode") final int exitCode,
                      @JsonProperty("IsErroredOnProcessing") final boolean errorOnProcessing,
                      @JsonProperty("ErrorMessage") final List<String> errorMessage,
                      @JsonProperty("ErrorDetails") final List<String> errorDetails)
    {
        this.parsedResults = parsedResults;
        this.exitCode = exitCode;
        this.errorOnProcessing = errorOnProcessing;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
    }
}
