package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.exception.ScanException;
import fr.tobby.tripnjoyback.model.ocr.space.ParsedResult;
import fr.tobby.tripnjoyback.model.ocr.space.ScanResult;
import fr.tobby.tripnjoyback.model.response.ScanResponse;
import fr.tobby.tripnjoyback.utils.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ScanService {

    private static final Logger logger = LoggerFactory.getLogger(ScanService.class);
    public static final Pattern PRICE_PATTERN = Pattern.compile("(-?\\d+[.,]\\d{2}) ?[$€£]?");

    private final String apiKey;

    public ScanService(@Value("${ocr.space.apikey}") final String apiKey)
    {
        this.apiKey = apiKey;
    }

    public ScanResponse scan() throws IOException
    {
        File file = new File("IMG_6350.jpeg");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("apikey", apiKey);
        LinkedMultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
//        data.add("url", "https://ocr.space/Content/Images/receipt-ocr-original.jpg");
        data.add("language", "eng");
        data.add("isOverlayRequired", "true");
        data.add("fileType", "jpg");
        data.add("isTable", "true");
        data.add("detectOrientation", "true");
//        data.add("file", new MultipartInputStreamFileResource(new FileInputStream(file), file.getName()));
        String encodedFile = Base64Utils.encodeToString(new FileInputStream(file).readAllBytes());
        data.add("base64Image", "data:image/jpg;base64," + encodedFile);

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
        logger.info("Parsed text: {}", parsedText);
        return parseContent(parsedText);
    }

    public ScanResponse parseContent(String content)
    {
        String[] lines = content.split("\t\r\n");
        Map<String, Float> items = new LinkedHashMap<>();
        for (final String line : lines)
        {
            Pair<String, Float> item = parseLine(line);
            if (item != null)
                items.put(item.left(), item.right());
        }
        return new ScanResponse(items, 0);
    }

    @Nullable
    public Pair<String, Float> parseLine(String line)
    {
        String[] columns = line.split("\t");

        // Search for price in columns
        for (String column : columns)
        {
            var matcher = PRICE_PATTERN.matcher(column);
            if (matcher.matches())
            {
                String priceRaw = matcher.group(1).replace(",", ".");
                float price = Float.parseFloat(priceRaw);
                return new Pair<>(columns[0], price);
            }
        }
        // If no price found, there is no item in the line
        return null;
    }
}



class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException
    {
        return -1;
    }
}