package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatchMakerTest {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private MatchMaker matchMaker;

    @BeforeEach
    void setUp()
    {
        matchMaker = new MatchMaker();
    }

    @Test
    void availabilitySameIntervalTest() throws ParseException
    {
        var a = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));

        float res = matchMaker.computeAvailabilityCorrelation(List.of(a), List.of(b));
        assertEquals(5, res);
    }

    @Test
    void availabilityDistinctTest() throws ParseException
    {
        var a = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("11-01-2000"));

        float res = matchMaker.computeAvailabilityCorrelation(List.of(a), List.of(b));
        assertEquals(0, res);
    }

    @Test
    void availabilityOverlapTest() throws ParseException
    {
        var a = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b = new AvailabilityAnswerModel(dateFormat.parse("03-01-2000"), dateFormat.parse("07-01-2000"));

        float res = matchMaker.computeAvailabilityCorrelation(List.of(a), List.of(b));
        assertEquals(3, res);
    }

    @Test
    void availabilitySecondDistinctIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("03-01-2000"), dateFormat.parse("07-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("10-01-2000"), dateFormat.parse("11-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("20-01-2000"), dateFormat.parse("21-01-2000"));

        float res = matchMaker.computeAvailabilityCorrelation(List.of(a1, a2), List.of(b1, b2));
        assertEquals(3, res);
    }

    @Test
    void availabilityFirstDistinctIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("10-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("11-01-2000"), dateFormat.parse("15-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("12-01-2000"), dateFormat.parse("21-01-2000"));

        float res = matchMaker.computeAvailabilityCorrelation(List.of(a1, a2), List.of(b1, b2));
        assertEquals(4, res);
    }

    @Test
    void availabilityMixedIntervalsTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("10-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("07-01-2000"), dateFormat.parse("15-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("13-01-2000"), dateFormat.parse("21-01-2000"));

        float res = matchMaker.computeAvailabilityCorrelation(List.of(a1, a2), List.of(b1, b2));
        assertEquals(4, res);
    }

    @Test
    void availabilitySecondSmallerIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("10-01-2000"), dateFormat.parse("11-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("10-01-2000"), dateFormat.parse("13-01-2000"));

        float res = matchMaker.computeAvailabilityCorrelation(List.of(a1, a2), List.of(b1, b2));
        assertEquals(5, res);
    }

    @Test
    void emptyIntervalTest() throws ParseException
    {
        assertThrows(IllegalArgumentException.class, () -> matchMaker.computeAvailabilityCorrelation(List.of(), List.of()));
    }

    @Test
    void inconsistentIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("02-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));

        // inconsistency, overlapping a1
        var a2 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("11-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("07-01-2000"));
        assertThrows(IllegalArgumentException.class, () -> matchMaker.computeAvailabilityCorrelation(List.of(a1, a2), List.of(b1, b2)));
    }
}