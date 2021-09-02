package pl.edu.pwr.wordnetloom.server.business.tracker;

import javax.inject.Singleton;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Singleton
public class TrackerDateService {

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public Date parseStringDateToDate(String stringDate) throws ParseException {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.parse(stringDate);
    }

    public LocalDateTime getLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public LocalDateTime getTodayDateTime() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    }

    private long localDateTimeToTimestampLong(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime).getTime();
    }

    public long getBeginOfDate(LocalDateTime localDateTime) {
        return localDateTimeToTimestampLong(localDateTime.with(LocalTime.MIN));
    }

    public long getEndOfDate(LocalDateTime localDateTime) {
        return localDateTimeToTimestampLong(localDateTime.with(LocalTime.MAX));
    }

    public long getBeginOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime().getTime();
    }

    public long getEndOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime().getTime();
    }
}
