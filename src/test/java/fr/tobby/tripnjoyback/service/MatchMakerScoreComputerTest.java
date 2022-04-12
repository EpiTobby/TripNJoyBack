package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.Gender;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.GenderAnswer;
import fr.tobby.tripnjoyback.model.request.anwsers.RangeAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.StaticAnswerModel;
import fr.tobby.tripnjoyback.repository.AnswersRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatchMakerScoreComputerTest {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private MatchMakerScoreComputer matchMakerScoreComputer;

    @BeforeEach
    void setUp()
    {
        matchMakerScoreComputer = new MatchMakerScoreComputer(Mockito.mock(ProfileRepository.class), Mockito.mock(UserRepository.class), Mockito.mock(AnswersRepository.class));
    }

    @Test
    void availabilitySameIntervalTest() throws ParseException
    {
        var a = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));

        float res = matchMakerScoreComputer.computeAvailabilityCorrelation(matchMakerScoreComputer.computeCommonAvailabilities(List.of(a), List.of(b)));
        assertEquals(5, res);
    }

    @Test
    void availabilityDistinctTest() throws ParseException
    {
        var a = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("11-01-2000"));

        float res = matchMakerScoreComputer.computeAvailabilityCorrelation(matchMakerScoreComputer.computeCommonAvailabilities(List.of(a), List.of(b)));
        assertEquals(0, res);
    }

    @Test
    void availabilityOverlapTest() throws ParseException
    {
        var a = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b = new AvailabilityAnswerModel(dateFormat.parse("03-01-2000"), dateFormat.parse("07-01-2000"));

        float res = matchMakerScoreComputer.computeAvailabilityCorrelation(matchMakerScoreComputer.computeCommonAvailabilities(List.of(a), List.of(b)));
        assertEquals(3, res);
    }

    @Test
    void availabilitySecondDistinctIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("03-01-2000"), dateFormat.parse("07-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("10-01-2000"), dateFormat.parse("11-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("20-01-2000"), dateFormat.parse("21-01-2000"));

        float res = matchMakerScoreComputer.computeAvailabilityCorrelation(matchMakerScoreComputer.computeCommonAvailabilities(List.of(a1, a2), List.of(b1, b2)));
        assertEquals(3, res);
    }

    @Test
    void availabilityFirstDistinctIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("10-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("11-01-2000"), dateFormat.parse("15-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("12-01-2000"), dateFormat.parse("21-01-2000"));

        float res = matchMakerScoreComputer.computeAvailabilityCorrelation(matchMakerScoreComputer.computeCommonAvailabilities(List.of(a1, a2), List.of(b1, b2)));
        assertEquals(4, res);
    }

    @Test
    void availabilityMixedIntervalsTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("10-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("07-01-2000"), dateFormat.parse("15-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("13-01-2000"), dateFormat.parse("21-01-2000"));

        float res = matchMakerScoreComputer.computeAvailabilityCorrelation(matchMakerScoreComputer.computeCommonAvailabilities(List.of(a1, a2), List.of(b1, b2)));
        assertEquals(4, res);
    }

    @Test
    void availabilitySecondSmallerIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));

        var a2 = new AvailabilityAnswerModel(dateFormat.parse("10-01-2000"), dateFormat.parse("11-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("10-01-2000"), dateFormat.parse("13-01-2000"));

        float res = matchMakerScoreComputer.computeAvailabilityCorrelation(matchMakerScoreComputer.computeCommonAvailabilities(List.of(a1, a2), List.of(b1, b2)));
        assertEquals(5, res);
    }

    @Test
    void emptyIntervalTest()
    {
        assertThrows(IllegalArgumentException.class, () -> matchMakerScoreComputer.computeCommonAvailabilities(List.of(), List.of()));
    }

    @Test
    void inconsistentIntervalTest() throws ParseException
    {
        var a1 = new AvailabilityAnswerModel(dateFormat.parse("02-01-2000"), dateFormat.parse("05-01-2000"));
        var b1 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("05-01-2000"));

        // inconsistency, overlapping a1
        var a2 = new AvailabilityAnswerModel(dateFormat.parse("01-01-2000"), dateFormat.parse("11-01-2000"));
        var b2 = new AvailabilityAnswerModel(dateFormat.parse("06-01-2000"), dateFormat.parse("07-01-2000"));
        assertThrows(IllegalArgumentException.class, () -> matchMakerScoreComputer.computeCommonAvailabilities(List.of(a1, a2), List.of(b1, b2)));
    }
}

class MatchMakerScoreComputerRangeTest {
    private MatchMakerScoreComputer matchMakerScoreComputer;

    @BeforeEach
    void setUp()
    {
        matchMakerScoreComputer = new MatchMakerScoreComputer(Mockito.mock(ProfileRepository.class), Mockito.mock(UserRepository.class), Mockito.mock(AnswersRepository.class));
    }

    @Test
    void rangeDistinctTest()
    {
        var a = new RangeAnswerModel(1, 5);
        var b = new RangeAnswerModel(6, 10);

        assertEquals(0, matchMakerScoreComputer.computeRangeScore(a, b));
    }

    @Test
    void rangeEqualsTest()
    {
        var a = new RangeAnswerModel(1, 5);
        var b = new RangeAnswerModel(1, 5);

        assertEquals(1, matchMakerScoreComputer.computeRangeScore(a, b));
    }

    @Test
    void rangeHalfOverlappingTest()
    {
        var a = new RangeAnswerModel(1, 6);
        var b = new RangeAnswerModel(4, 9);

        assertEquals(0.5, matchMakerScoreComputer.computeRangeScore(a, b));
    }

    @Test
    void rangeHalfContainedTest()
    {
        var a = new RangeAnswerModel(1, 6);
        var b = new RangeAnswerModel(1, 3);

        assertEquals(0.75, matchMakerScoreComputer.computeRangeScore(a, b));
    }
}

class MatchMakerScoreComputerStaticChoiceTest {
    private MatchMakerScoreComputer matchMakerScoreComputer;

    @BeforeEach
    void setUp()
    {
        matchMakerScoreComputer = new MatchMakerScoreComputer(Mockito.mock(ProfileRepository.class), Mockito.mock(UserRepository.class), Mockito.mock(AnswersRepository.class));
    }

    @Test
    void distinctTest()
    {
        float res = matchMakerScoreComputer.computeStaticChoiceScore(List.of(TwoValueChoice.B), List.of(TwoValueChoice.A), TwoValueChoice.NONE);
        assertEquals(0f, res, 0.001f); // Delta for comparing floats
    }

    @Test
    void equalsTest()
    {
        float res = matchMakerScoreComputer.computeStaticChoiceScore(List.of(TwoValueChoice.A), List.of(TwoValueChoice.A), TwoValueChoice.NONE);
        assertEquals(1f, res, 0.001f);
    }

    @Test
    void noPrefTest()
    {
        float res = matchMakerScoreComputer.computeStaticChoiceScore(List.of(TwoValueChoice.NONE), List.of(TwoValueChoice.A), TwoValueChoice.NONE);
        assertEquals(0.8f, res, 0.001f); // 0.8 = hardcoded value if one user has no preference
    }

    @Test
    void multiValueOneDiffOneCommonTest()
    {
        float res = matchMakerScoreComputer.computeStaticChoiceScore(List.of(FourValueChoice.A, FourValueChoice.B), List.of(FourValueChoice.B, FourValueChoice.C), FourValueChoice.NONE);
        assertEquals(0.5f, res, 0.001f);
    }

    @Test
    void multiValueOneDiffTwoCommonTest()
    {
        float res = matchMakerScoreComputer.computeStaticChoiceScore(List.of(FourValueChoice.A, FourValueChoice.B, FourValueChoice.C),
                List.of(FourValueChoice.B, FourValueChoice.C),
                FourValueChoice.NONE);
        assertEquals(0.8f, res, 0.001f);
    }

    @Test
    void multiValueTwoDiffOneCommonTest()
    {
        float res = matchMakerScoreComputer.computeStaticChoiceScore(List.of(FourValueChoice.A, FourValueChoice.B, FourValueChoice.C),
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

class MatchMakerScoreComputerGender {
    private MatchMakerScoreComputer matchMakerScoreComputer;

    @BeforeEach
    void setUp()
    {
        matchMakerScoreComputer = new MatchMakerScoreComputer(Mockito.mock(ProfileRepository.class), Mockito.mock(UserRepository.class), Mockito.mock(AnswersRepository.class));
    }

    @Test
    void distinctTest()
    {
        float res = matchMakerScoreComputer.compareGender(Gender.MALE, Gender.FEMALE, GenderAnswer.MALE, GenderAnswer.FEMALE);
        assertEquals(0f, res, 0.001f);
    }

    @Test
    void matchingSameGenderTest()
    {
        float res = matchMakerScoreComputer.compareGender(Gender.MALE, Gender.MALE, GenderAnswer.MALE, GenderAnswer.MALE);
        assertEquals(1f, res, 0.001f);
    }

    @Test
    void noPrefTest()
    {
        float res = matchMakerScoreComputer.compareGender(Gender.MALE, Gender.NOT_SPECIFIED, GenderAnswer.MIXED, GenderAnswer.MIXED);
        assertEquals(0.8f, res, 0.001f);
    }

    @Test
    void matchingAndNoPrefTest()
    {
        float res = matchMakerScoreComputer.compareGender(Gender.MALE, Gender.MALE, GenderAnswer.MALE, GenderAnswer.MIXED);
        assertEquals(0.8f, res, 0.001f);
    }

    @Test
    void noMatchTest()
    {
        float res = matchMakerScoreComputer.compareGender(Gender.MALE, Gender.MALE, GenderAnswer.MALE, GenderAnswer.FEMALE);
        assertEquals(0f, res, 0.001f);
    }
}