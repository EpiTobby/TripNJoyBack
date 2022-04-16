package fr.tobby.tripnjoyback.model.request.anwsers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import fr.tobby.tripnjoyback.exception.BadAvailabilityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class AvailabilityAnswerModel implements AnswerModel {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;

    @JsonProperty("startDate")
    public String getStartDateStr()
    {
        return new SimpleDateFormat(DATE_FORMAT).format(startDate);
    }

    @JsonProperty("endDate")
    public String getEndDateStr()
    {
        return new SimpleDateFormat(DATE_FORMAT).format(endDate);
    }

    @JsonIgnore
    public Date getStartDate()
    {
        return startDate;
    }

    @JsonIgnore
    public Date getEndDate()
    {
        return endDate;
    }

    @JsonProperty("startDate")
    @JsonFormat(timezone = "GMT+01:00")
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
        if (endDate != null && this.startDate.after(endDate))
            throw new BadAvailabilityException("Start date cannot be after end date");
    }

    @JsonProperty("endDate")
    @JsonFormat(timezone = "GMT+01:00")
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
        if (startDate != null && this.endDate.before(startDate))
            throw new BadAvailabilityException("Start date cannot be after end date");
    }

    public static AvailabilityAnswerModel of(String startDate, String endDate)
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try
        {
            dateFormat.setTimeZone(TimeZone.getDefault());
            return new AvailabilityAnswerModel(dateFormat.parse(startDate), dateFormat.parse(endDate));
        }
        catch (Exception e)
        {
            throw new BadAvailabilityException("Cannot parse availability");
        }
    }
}
