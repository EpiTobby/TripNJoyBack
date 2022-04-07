package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.MatchMakingUserModel;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.RangeAnswerModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
public class MatchMaker {

    float computeCorrelation(@NotNull final MatchMakingUserModel userA, @NotNull final MatchMakingUserModel userB)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Compute the maximum number of successive shared days between the two given sorted interval lists
     */
    float computeAvailabilityCorrelation(@NotNull List<AvailabilityAnswerModel> a, List<AvailabilityAnswerModel> b)
    {
        if (a.isEmpty() || b.isEmpty())
            throw new IllegalArgumentException("Availabilities cannot be empty");

        int res = 0;

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
            int days = 1 + (int) Duration.between(start.toInstant(), end.toInstant()).toDays();
            res = Math.max(res, days);

            if (isABefore)
                indexA++;
            else
                indexB++;
        }

        return res;
    }

    /**
     * Compute the similarity between to ranges, i.e, the ratio of shared area
     * @return score between 0 and 1. 0 for distinct ranges, 1 for equal ranges
     */
    float computeRangeScore(@NotNull final RangeAnswerModel a, @NotNull final RangeAnswerModel b)
    {
        int min = Math.max(a.getMinValue(), b.getMinValue());
        int max = Math.min(a.getMaxValue(), b.getMaxValue());
        if (min > max)
            return 0;

        float overlappingRange = max - min + 1f;
        float scoreA = overlappingRange / (a.getMaxValue() - a.getMinValue() + 1f);
        float scoreB = overlappingRange / (b.getMaxValue() - b.getMinValue() + 1f);

        return 0.5f * scoreA + 0.5f * scoreB;
    }
}
