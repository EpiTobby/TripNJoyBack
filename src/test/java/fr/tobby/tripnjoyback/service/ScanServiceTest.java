package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.response.ScanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScanServiceTest {

    private ScanService scanService;

    @BeforeEach
    void setUp()
    {
        scanService = new ScanService("");
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
        ScanResponse scanResponse = scanService.parseContent("");

        assertEquals(0, scanResponse.getTotal());
        assertEquals(0, scanResponse.getItems().size());
    }

    @Test
    void parseContentOneLineTest()
    {
        ScanResponse scanResponse = scanService.parseContent("PET Toy\t2.00");

        assertEquals(0, scanResponse.getTotal());
        assertEquals(1, scanResponse.getItems().size());
    }

    @Test
    void parseContentTwoLinesTest()
    {
        ScanResponse scanResponse = scanService.parseContent("PET Toy\t2.00\t\r\nT-shirt\t16,05€");

        assertEquals(0, scanResponse.getTotal());
        assertEquals(2, scanResponse.getItems().size());
        assertEquals(2, scanResponse.getItems().get("PET Toy"));
        assertEquals(16.05f, scanResponse.getItems().get("T-shirt"));
    }

    @Test
    void parseContentTwoLinesWithUselessLinesTest()
    {
        ScanResponse scanResponse = scanService.parseContent("MONOPRIX\t\r\nPET Toy\t2.00\t\r\nHello\tWorld!\t\r\nT-shirt\t16,05€\t\r\nGood Bye!");

        assertEquals(0, scanResponse.getTotal());
        assertEquals(2, scanResponse.getItems().size());
        assertEquals(2, scanResponse.getItems().get("PET Toy"));
        assertEquals(16.05f, scanResponse.getItems().get("T-shirt"));
    }
}