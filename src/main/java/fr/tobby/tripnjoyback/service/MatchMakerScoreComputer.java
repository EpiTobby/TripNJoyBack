package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.Gender;
import fr.tobby.tripnjoyback.model.MatchMakingUserModel;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class MatchMakerScoreComputer {

    /**
     * Computes the matching score between 2 users. Does not check if conditions are compatible, apart from availabilities and duration
     */
    float computeMatchingScore(@NotNull final ProfileModel profA, @NotNull final ProfileModel profB)
    {
        float availabilityCorr = computeAvailabilityCorrelation(computeCommonAvailabilities(profA.getAvailabilities(), profB.getAvailabilities()));
        if (availabilityCorr == 0) // common availability is mandatory
            return 0;

        Optional<RangeAnswerModel> commonDuration = computeCommonRange(profA.getDuration(), profB.getDuration());
        if (commonDuration.isEmpty() || availabilityCorr < commonDuration.get().getMinValue())
            return 0;

        float res = availabilityCorr;
        res += computeRangeScore(profA.getDuration(), profB.getDuration());
        res += computeRangeScore(profA.getBudget(), profB.getBudget());
        res += computeStaticChoiceScore(profA.getDestinationTypes(), profB.getDestinationTypes(), DestinationTypeAnswer.NO_PREFERENCE);
        res += computeRangeScore(profA.getAges(), profB.getAges());
        res += computeStaticChoiceScore(profA.getTravelWithPersonFromSameCity(), profB.getTravelWithPersonFromSameCity(), YesNoAnswer.NO_PREFERENCE);
        res += computeStaticChoiceScore(profA.getTravelWithPersonFromSameCountry(), profB.getTravelWithPersonFromSameCountry(), YesNoAnswer.NO_PREFERENCE);
        res += computeStaticChoiceScore(profA.getTravelWithPersonSameLanguage(), profB.getTravelWithPersonSameLanguage(), YesNoAnswer.NO_PREFERENCE);

        res += computeStaticChoiceScore(profA.getGender(), profB.getGender(), GenderAnswer.MIXED);
        res += computeRangeScore(profA.getGroupeSize(), profB.getGroupeSize());
        res += computeStaticChoiceScore(profA.getChillOrVisit(), profB.getChillOrVisit(), ChillOrVisitAnswer.NO_PREFERENCE);
        res += computeStaticChoiceScore(profA.getAboutFood(), profB.getAboutFood(), AboutFoodAnswer.NO_PREFERENCE);
        res += computeStaticChoiceScore(profA.getGoOutAtNight(), profB.getGoOutAtNight(), YesNoAnswer.NO_PREFERENCE);
        res += computeStaticChoiceScore(profA.getSport(), profB.getSport(), YesNoAnswer.NO_PREFERENCE);

        return res;
    }

    boolean isUserCompatible(@NotNull final ProfileModel profile, @NotNull final MatchMakingUserModel user)
    {
        if (profile.getGender() != GenderAnswer.MIXED && profile.getGender().toGender() != user.getGender())
            return false;
        if (!profile.getAges().isInRange(user.getAge()))
            return false;
        if (computeCommonRange(profile.getGroupeSize(), user.getProfile().getGroupeSize()).isEmpty())
            return false;
        return true;
    }

    /**
     * Compute the maximum number of successive shared days between the two given sorted interval lists. 0 if no matching period
     */
    float computeAvailabilityCorrelation(@NotNull List<AvailabilityAnswerModel> commonAvailability)
    {
        return commonAvailability.stream()
                                 .map(availability -> 1 + Duration.between(availability.getStartDate().toInstant(), availability.getEndDate().toInstant()).toDays())
                                 .max(Long::compare)
                                 .orElse(0L);
    }

    @NotNull
    List<AvailabilityAnswerModel> computeCommonAvailabilities(@NotNull List<AvailabilityAnswerModel> a, List<AvailabilityAnswerModel> b)
    {
        if (a.isEmpty() || b.isEmpty())
            throw new IllegalArgumentException("Availabilities cannot be empty");

        List<AvailabilityAnswerModel> res = new ArrayList<>();

        int indexA = 0;
        int indexB = 0;

        Date start = null;
        while (indexA < a.size() && indexB < b.size())
        {
            AvailabilityAnswerModel intervalA = a.get(indexA);
            AvailabilityAnswerModel intervalB = b.get(indexB);

            // FIXME: don't check sorting ? (for performance)
            if (start != null && (intervalA.getStartDate().before(start) || intervalB.getStartDate().before(start)))
                throw new IllegalArgumentException("Unsorted interval list");

            // If not overlapping, skipping to next interval
            if (intervalA.getStartDate().after(intervalB.getEndDate()))
            {
                indexB++;
                continue;
            }
            else if (intervalB.getStartDate().after(intervalA.getEndDate()))
            {
                indexA++;
                continue;
            }
            // Overlapping, get shared start & end
            start = intervalA.getStartDate().before(intervalB.getStartDate())
                    ? intervalB.getStartDate()
                    : intervalA.getStartDate();
            boolean isABefore = intervalA.getEndDate().before(intervalB.getEndDate());
            Date end = isABefore
                       ? intervalA.getEndDate()
                       : intervalB.getEndDate();
            res.add(new AvailabilityAnswerModel(start, end));

            if (isABefore)
                indexA++;
            else
                indexB++;
        }

        return res;
    }

    /**
     * Compute the similarity between to ranges, i.e, the ratio of shared area
     *
     * @return score between 0 and 1. 0 for distinct ranges, 1 for equal ranges
     */
    float computeRangeScore(@NotNull final RangeAnswerModel a, @NotNull final RangeAnswerModel b)
    {
        return computeCommonRange(a, b)
                .map(range -> {
                    float overlappingRange = range.getMaxValue() - range.getMinValue() + 1f;
                    float scoreA = overlappingRange / (a.getMaxValue() - a.getMinValue() + 1f);
                    float scoreB = overlappingRange / (b.getMaxValue() - b.getMinValue() + 1f);

                    return 0.5f * scoreA + 0.5f * scoreB;
                })
                .orElse(0f);
    }

    @NotNull
    Optional<RangeAnswerModel> computeCommonRange(@NotNull final RangeAnswerModel a, @NotNull final RangeAnswerModel b)
    {
        int min = Math.max(a.getMinValue(), b.getMinValue());
        int max = Math.min(a.getMaxValue(), b.getMaxValue());
        if (min > max)
            return Optional.empty();
        return Optional.of(new RangeAnswerModel(min, max));
    }

    /**
     * Compute the similarity between to list of answers, i.e, the ratio of shared answers
     *
     * @return score between 0 and 1. 0 for distinct choices, 1 for equal choices
     */
    <T extends StaticAnswerModel> float computeStaticChoiceScore(@NotNull final List<T> a, @NotNull final List<T> b, T noPreferenceValue)
    {
        if (a.isEmpty() || b.isEmpty() || (a.size() == 1 && a.get(0).equals(noPreferenceValue)) || (b.size() == 1 && b.get(0).equals(noPreferenceValue)))
            return 0.8f;

        Set<T> values = new HashSet<>(a);
        int common = 0;
        for (final T el : b)
        {
            if (!values.add(el))
                common++;
        }
        return (common * 2f) / (a.size() + b.size());
    }

    <T extends StaticAnswerModel> float computeStaticChoiceScore(@NotNull final T a, @NotNull final T b, T noPreferenceValue)
    {
        return computeStaticChoiceScore(List.of(a), List.of(b), noPreferenceValue);
    }

    /**
     * Match the gender preferences.
     *
     * @param genderA Gender of user A
     * @param genderB Gender of user B
     * @param prefA Preference of user A
     * @param prefB Preference of user B
     *
     * @return 0 if at least one preference is not satisfied. 1 if all are satisfied. 0.8 if matches but at least one user has no preference.
     */
    float compareGender(@Nullable Gender genderA, @Nullable Gender genderB, @NotNull GenderAnswer prefA, @NotNull GenderAnswer prefB)
    {
        if (prefA == GenderAnswer.MIXED && prefB == GenderAnswer.MIXED)
            return 0.8f;

        if (prefA != GenderAnswer.MIXED && prefA.toGender() != genderB)
            return 0f;
        if (prefB != GenderAnswer.MIXED && prefB.toGender() != genderA)
            return 0f;
        return prefA != GenderAnswer.MIXED && prefB != GenderAnswer.MIXED ? 1 : 0.8f;
    }
}
