package fr.tripnjoy.expenses.repository;

import java.util.List;

public interface OcrScanner {

    List<String> scanLines(String url);
}
