package fr.tobby.tripnjoyback.model.request.anwsers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import fr.tobby.tripnjoyback.exception.BadAvailabilityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class AvailabilityAnswerModel implements AnswerModel {
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;

    @JsonProperty("startDate")
    public void setStartDate(String startDate){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try{
            this.startDate = dateFormat.parse(startDate);
            if (endDate != null && this.startDate.after(endDate))
                throw new BadAvailabilityException("Start date cannot be after end date");
        }
        catch (ParseException e){
            throw new BadAvailabilityException("Cannot parse start date");
        }
    }

    @JsonProperty("endDate")
    public void setEndDate(String endDate){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try{
            this.endDate = dateFormat.parse(endDate);
            if (startDate != null && this.endDate.before(startDate))
                throw new BadAvailabilityException("Start date cannot be after end date");
        }
        catch (ParseException e){
            throw new BadAvailabilityException("Cannot parse end date");
        }
    }

    public static AvailabilityAnswerModel of(String startDate, String endDate){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try{
            dateFormat.setTimeZone(TimeZone.getDefault());
            return new AvailabilityAnswerModel(dateFormat.parse(startDate), dateFormat.parse(endDate));
        }
        catch (Exception e){
            throw new BadAvailabilityException("Cannot parse availability");
        }
    }

    private long getNumberOfDaysInCommon(AvailabilityAnswerModel other){
        AvailabilityAnswerModel earliest = startDate.before(other.getStartDate()) ? this : other;
        AvailabilityAnswerModel latest = startDate.after(other.getStartDate()) ? this : other;
        if (latest.getStartDate().after(earliest.getEndDate())){
            return -1;
        }
        long diff = Math.abs(earliest.getEndDate().getTime() - latest.getStartDate().getTime());
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
