package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.ocr.space.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class ScanService {

    private static final Logger logger = LoggerFactory.getLogger(ScanService.class);

    private final String apiKey;

    public ScanService(@Value("${ocr.space.apikey}") final String apiKey)
    {
        this.apiKey = apiKey;
    }

    public ScanResult scan()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("apikey", apiKey);
        LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("url", "https://ocr.space/Content/Images/receipt-ocr-original.jpg");
        data.add("language", "eng");
        data.add("isOverlayRequired", "true");
        data.add("fileType", "jpg");
        data.add("isTable", "true");

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(data, headers);

        ResponseEntity<ScanResult> response = restTemplate.postForEntity(
                "https://api.ocr.space/parse/image",
                entity,
                ScanResult.class);
        logger.info("Code {}", response.getStatusCodeValue());

        return response.getBody();
    }
}
