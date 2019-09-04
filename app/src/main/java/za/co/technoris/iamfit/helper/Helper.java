package za.co.technoris.iamfit.helper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.provider.Settings.System.DATE_FORMAT;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Helper {

    private static final String FORMAT = "%02d:%02d:%02d";
    private static final Locale enZA = new Locale("en", "ZA");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd hh:mm", enZA);

    public static String parseTime(int minutes) {
        return String.format(enZA, FORMAT,
                MILLISECONDS.toHours((long)minutes*60000),
                MILLISECONDS.toMinutes((long)minutes*60000) - HOURS.toMinutes(MILLISECONDS.toHours((long)minutes*60000)),
                MILLISECONDS.toSeconds((long)minutes*60000) - MINUTES.toSeconds(MILLISECONDS.toMinutes((long)minutes*60000)));
    }

    public static String parseTime(int hours, int minutes) {
        return String.format(enZA, FORMAT, MILLISECONDS.toHours((long)(hours*(3.6e+6))),
                MILLISECONDS.toMinutes((long)minutes*60000) - HOURS.toMinutes(MILLISECONDS.toHours((long)minutes*60000)),
                MILLISECONDS.toSeconds((long)minutes*60000) - MINUTES.toSeconds(MILLISECONDS.toMinutes((long)minutes*60000)));
    }

    public static String parseDate(long date) {
        return simpleDateFormat.format((date*60000));
    }

    public static String getStartTime(int endHour, int endMinute, int minutes) {
        return parseDate(Date.parse(parseTime(endHour, endMinute)) - Date.parse(parseTime(minutes)));
    }
}
