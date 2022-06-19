package fr.tobby.tripnjoyback.repository.scan;

import fr.tobby.tripnjoyback.exception.ScanException;
import fr.tobby.tripnjoyback.model.ocr.space.ParsedResult;
import fr.tobby.tripnjoyback.model.ocr.space.ScanResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class OcrSpaceScanner implements OcrScanner {

    private final String apiKey;

    public OcrSpaceScanner(@Value("${ocr.space.apikey}") final String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public List<String> scanLines(final String url)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("apikey", apiKey);
        LinkedMultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("url", url);
        data.add("language", "eng");
        data.add("isOverlayRequired", "true");
        data.add("fileType", "jpg");
        data.add("isTable", "true");
        data.add("detectOrientation", "true");

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(data, headers);

        ResponseEntity<ScanResult> response = restTemplate.postForEntity(
                "https://api.ocr.space/parse/image",
                entity,
                ScanResult.class);
        if (response.getStatusCodeValue() != 200)
            throw new ScanException("Received unexpected status code " + response.getStatusCodeValue());

        ScanResult scan = response.getBody();
        if (scan == null)
            throw new ScanException("Failed to scan, null body ");

        if (scan.getExitCode() != 1)
            throw new ScanException("Failed to scan. " + scan.getErrorMessage() + ". Details: " + scan.getErrorDetails());

        List<ParsedResult> results = scan.getParsedResults();
        if (results.isEmpty())
            throw new ScanException("Failed to scan, no content detected ");

        String parsedText = results.get(0).getParsedText();
        String[] lines = parsedText.split("\t\r\n");
        return Arrays.asList(lines);
    }
}
