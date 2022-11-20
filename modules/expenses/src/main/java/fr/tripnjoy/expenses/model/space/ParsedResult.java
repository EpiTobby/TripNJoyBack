package fr.tripnjoy.expenses.model.space;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedResult {
    private final int exitCode;
    private final String parsedText;
    private final String errorMessage;
    private final String errorDetails;

    public ParsedResult(@JsonProperty("FileParseExitCode") final int exitCode,
                        @JsonProperty("ParsedText") final String parsedText,
                        @JsonProperty("ErrorMessage") final String errorMessage,
                        @JsonProperty("ErrorDetails") final String errorDetails)
    {
        this.exitCode = exitCode;
        this.parsedText = parsedText;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
    }
}
