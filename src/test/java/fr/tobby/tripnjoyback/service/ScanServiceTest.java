package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.response.ScanResponse;
import fr.tobby.tripnjoyback.repository.scan.OcrScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScanServiceTest {

    private ScanService scanService;

    @BeforeEach
    void setUp()
    {
        scanService = new ScanService(Mockito.mock(OcrScanner.class));
    }

    @Test
    void parseLineOcrSpaceExampleTest()
    {
        var line = scanService.parseLine("PET Toy\t004747571658\t1.97\tx");

        assertNotNull(line);
        assertEquals("PET Toy", line.left());
        assertEquals(1.97f, line.right());
    }

    @Test
    void parseLineFrenchTest()
    {
        var line = scanService.parseLine("PET Toy\t2,00€");

        assertNotNull(line);
        assertEquals("PET Toy", line.left());
        assertEquals(2, line.right());
    }

    @Test
    void parseLineSimpleTest()
    {
        var line = scanService.parseLine("PET Toy\t2.00");

        assertNotNull(line);
        assertEquals("PET Toy", line.left());
        assertEquals(2, line.right());
    }

    @Test
    void parseLineNoItemTest()
    {
        var line = scanService.parseLine("Vente à emporter");

        assertNull(line);
    }

    @Test
    void parseEmptyTest()
    {
        ScanResponse scanResponse = scanService.parseContent(List.of());

        assertEquals(0, scanResponse.getTotal());
        assertEquals(0, scanResponse.getItems().size());
    }

    @Test
    void parseContentOneLineTest()
    {
        ScanResponse scanResponse = scanService.parseContent(List.of("PET Toy\t2.00"));

        assertEquals(0, scanResponse.getTotal());
        assertEquals(1, scanResponse.getItems().size());
    }

    @Test
    void parseContentTwoLinesTest()
    {
        ScanResponse scanResponse = scanService.parseContent(List.of("PET Toy\t2.00", "T-shirt\t16,05€"));

        assertEquals(0, scanResponse.getTotal());
        assertEquals(2, scanResponse.getItems().size());
        assertEquals(2, scanResponse.getItems().get("PET Toy"));
        assertEquals(16.05f, scanResponse.getItems().get("T-shirt"));
    }

    @Test
    void parseContentTwoLinesWithUselessLinesTest()
    {
        ScanResponse scanResponse = scanService.parseContent(List.of("MONOPRIX", "PET Toy\t2.00", "", "Hello\tWorld!", "T-shirt\t16,05€", "Good Bye!"));

        assertEquals(0, scanResponse.getTotal());
        assertEquals(2, scanResponse.getItems().size());
        assertEquals(2, scanResponse.getItems().get("PET Toy"));
        assertEquals(16.05f, scanResponse.getItems().get("T-shirt"));
    }

    @Test
    void parseContentWithTotalTest()
    {
        ScanResponse scanResponse = scanService.parseContent(List.of("PET Toy\t2.00", "T-shirt\t16,05€", "Total\t18.05"));

        assertEquals(18.05f, scanResponse.getTotal());
        assertEquals(2, scanResponse.getItems().size());
        assertEquals(2, scanResponse.getItems().get("PET Toy"));
        assertEquals(16.05f, scanResponse.getItems().get("T-shirt"));
    }

    @Test
    void parseContentWithTotalAndInconsistentArticlesTest()
    {
        ScanResponse scanResponse = scanService.parseContent(List.of("PET Toy\t2.00", "T-shirt\t16,05€", "Before taxes\t18.05", "Total TTC\t19.42"));

        assertEquals(19.42f, scanResponse.getTotal());
        assertEquals(2, scanResponse.getItems().size());
        assertEquals(2, scanResponse.getItems().get("PET Toy"));
        assertEquals(16.05f, scanResponse.getItems().get("T-shirt"));
    }

    @Test
    void parseContentWithTotalAndTaxesTest()
    {
        ScanResponse scanResponse = scanService.parseContent(List.of("PET Toy\t2.00", "T-shirt\t16,05€", "Before taxes\t18.05", "Taxes\t1,37", "Total TTC\t19.42"));

        assertEquals(19.42f, scanResponse.getTotal());
        assertEquals(3, scanResponse.getItems().size());
        assertEquals(2, scanResponse.getItems().get("PET Toy"));
        assertEquals(16.05f, scanResponse.getItems().get("T-shirt"));
        assertEquals(1.37f, scanResponse.getItems().get("Taxes"));
    }
}