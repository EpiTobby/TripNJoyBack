package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.response.ScanResponse;
import fr.tobby.tripnjoyback.repository.scan.OcrScanner;
import fr.tobby.tripnjoyback.utils.Pair;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ScanService {

    public static final Pattern PRICE_PATTERN = Pattern.compile("(-?\\d+[.,]\\d{2}) ?[$€£]?");

    private final OcrScanner ocrScanner;

    public ScanService(final OcrScanner ocrScanner)
    {
        this.ocrScanner = ocrScanner;
    }


    public ScanResponse scan(String url)
    {
        List<String> lines = this.ocrScanner.scanLines(url);
        return parseContent(lines);
    }

    public ScanResponse parseContent(List<String> lines)
    {
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
