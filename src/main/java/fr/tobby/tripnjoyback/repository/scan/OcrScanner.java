package fr.tobby.tripnjoyback.repository.scan;

import java.util.List;

public interface OcrScanner {

    List<String> scanLines(String url);
}
