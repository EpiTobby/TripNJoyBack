package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.response.ScanResponse;
import fr.tobby.tripnjoyback.repository.scan.OcrScanner;
import fr.tobby.tripnjoyback.utils.Pair;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class ScanService {

    public static final Pattern PRICE_PATTERN = Pattern.compile("(.* )?(-?[\\dOo]+ ?[.,] ?[\\dOo]{2}) ?[$€£]?.*");

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
        float total = 0;
        for (final String line : lines)
        {
            Pair<String, Float> item = parseLine(line);
            if (item != null)
            {
                if (item.left().toUpperCase(Locale.ROOT).contains("TOTAL"))
                    total = item.right();
                else
                    items.put(item.left(), item.right());
            }
        }
        // If we found a `total` field, remove all articles that do not match the total
        if (total > 0)
            removeInconsistentArticles(items, total);
        return new ScanResponse(items, total);
    }

    private void removeInconsistentArticles(Map<String, Float> items, float expectedTotal)
    {
        float sum = 0;
        for (var it = items.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Float> entry = it.next();
            if (sum + entry.getValue() > expectedTotal)
                it.remove();
            else
                sum += entry.getValue();
        }
    }

    @Nullable
    public Pair<String, Float> parseLine(String line)
    {
        String[] columns = line.split("\t");

        // Search for price in columns
        int columnIndex = 0;
        for (String column : columns)
        {
            Pair<String, Float> itemName = extractItem(columns, columnIndex, column);
            if (itemName != null)
                return itemName;
            columnIndex++;
        }
        // If no price found, there is no item in the line
        StringJoiner joiner = new StringJoiner(" ");
        for (final String column : columns)
            joiner.add(column);
        return extractItem(columns, columnIndex, joiner.toString());
    }

    @Nullable
    private Pair<String, Float> extractItem(final String[] columns, final int columnIndex, final String column)
    {
        var matcher = PRICE_PATTERN.matcher(column);
        if (matcher.matches())
        {
            String priceRaw = matcher.group(2).replace(",", ".")
                                     .replaceAll("[Oo]", "0")
                                     .replace(" ", "");
            float price = Float.parseFloat(priceRaw);
            String itemName = columns[0].length() <= 3 && columnIndex > 1
                              ? columns[0] + " " + columns[1]
                              : columns[0];
            return new Pair<>(itemName, price);
        }
        return null;
    }
}
