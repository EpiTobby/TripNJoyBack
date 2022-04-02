package fr.tobby.tripnjoyback.model.request.anwsers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class AvailabilityAnswerModel implements AnswerModel {
    private Date startDate;
    private Date endDate;

    @JsonProperty("startDate")
    public void setStartDate(String startDate){
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        try{
            this.startDate = dateFormat.parse(startDate);
        }
        catch (Exception e){
        }
    }

    @JsonProperty("endDate")
    public void setEndDate(String endDate){
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        try{
            this.endDate = dateFormat.parse(endDate);
        }
        catch (Exception e){
        }
    }

    public static AvailabilityAnswerModel of(String startDate, String endDate){
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        try{
            return new AvailabilityAnswerModel(dateFormat.parse(startDate), dateFormat.parse(endDate));
        }
        catch (Exception e){
            return null;
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
