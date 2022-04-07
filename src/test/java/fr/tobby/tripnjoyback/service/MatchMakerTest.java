package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.RangeAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.StaticAnswerModel;
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
    void emptyIntervalTest()
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

class MatchMakerRangeTest {
    private MatchMaker matchMaker;

    @BeforeEach
    void setUp()
    {
        matchMaker = new MatchMaker();
    }

    @Test
    void rangeDistinctTest()
    {
        var a = new RangeAnswerModel(1, 5);
        var b = new RangeAnswerModel(6, 10);

        assertEquals(0, matchMaker.computeRangeScore(a, b));
    }

    @Test
    void rangeEqualsTest()
    {
        var a = new RangeAnswerModel(1, 5);
        var b = new RangeAnswerModel(1, 5);

        assertEquals(1, matchMaker.computeRangeScore(a, b));
    }

    @Test
    void rangeHalfOverlappingTest()
    {
        var a = new RangeAnswerModel(1, 6);
        var b = new RangeAnswerModel(4, 9);

        assertEquals(0.5, matchMaker.computeRangeScore(a, b));
    }

    @Test
    void rangeHalfContainedTest()
    {
        var a = new RangeAnswerModel(1, 6);
        var b = new RangeAnswerModel(1, 3);

        assertEquals(0.75, matchMaker.computeRangeScore(a, b));
    }
}

class MatchMakerStaticChoiceTest {
    private MatchMaker matchMaker;

    @BeforeEach
    void setUp()
    {
        matchMaker = new MatchMaker();
    }

    @Test
    void distinctTest()
    {
        float res = matchMaker.computeStaticChoiceScore(List.of(TwoValueChoice.B), List.of(TwoValueChoice.A), TwoValueChoice.NONE);
        assertEquals(0f, res, 0.001f); // Delta for comparing floats
    }

    @Test
    void equalsTest()
    {
        float res = matchMaker.computeStaticChoiceScore(List.of(TwoValueChoice.A), List.of(TwoValueChoice.A), TwoValueChoice.NONE);
        assertEquals(1f, res, 0.001f);
    }

    @Test
    void noPrefTest()
    {
        float res = matchMaker.computeStaticChoiceScore(List.of(TwoValueChoice.NONE), List.of(TwoValueChoice.A), TwoValueChoice.NONE);
        assertEquals(0.8f, res, 0.001f); // 0.8 = hardcoded value if one user has no preference
    }

    @Test
    void multiValueOneDiffOneCommonTest()
    {
        float res = matchMaker.computeStaticChoiceScore(List.of(FourValueChoice.A, FourValueChoice.B), List.of(FourValueChoice.B, FourValueChoice.C), FourValueChoice.NONE);
        assertEquals(0.5f, res, 0.001f);
    }

    @Test
    void multiValueOneDiffTwoCommonTest()
    {
        float res = matchMaker.computeStaticChoiceScore(List.of(FourValueChoice.A, FourValueChoice.B, FourValueChoice.C),
                List.of(FourValueChoice.B, FourValueChoice.C),
                FourValueChoice.NONE);
        assertEquals(0.8f, res, 0.001f);
    }

    @Test
    void multiValueTwoDiffOneCommonTest()
    {
        float res = matchMaker.computeStaticChoiceScore(List.of(FourValueChoice.A, FourValueChoice.B, FourValueChoice.C),
                List.of(FourValueChoice.C, FourValueChoice.D),
                FourValueChoice.NONE);
        assertEquals(0.4f, res, 0.001f);
    }

    private enum TwoValueChoice implements StaticAnswerModel {
        A,
        B,
        NONE
    }

    private enum FourValueChoice implements StaticAnswerModel {
        A,
        B,
        C,
        D,
        NONE
    }
}