package za.co.technoris.iamfit.helper;

import java.util.Locale;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Helper {

    private static final String FORMAT = "%02d:%02d:%02d";

    public static String parseTime(int minutes) {
        return String.format(new Locale("en", "ZA"), FORMAT,
                MILLISECONDS.toHours((long)minutes*60000),
                MILLISECONDS.toMinutes((long)minutes*60000) - HOURS.toMinutes(MILLISECONDS.toHours((long)minutes*60000)),
                MILLISECONDS.toSeconds((long)minutes*60000) - MINUTES.toSeconds(MILLISECONDS.toMinutes((long)minutes*60000)));
    }

    public static String parseTime(int hours, int minutes) {
        return String.format(new Locale("en", "ZA"), FORMAT,
                MILLISECONDS.toHours((long)(hours*(3.6e+6))),
                MILLISECONDS.toMinutes((long)minutes*60000) - HOURS.toMinutes(MILLISECONDS.toHours((long)minutes*60000)),
                MILLISECONDS.toSeconds((long)minutes*60000) - MINUTES.toSeconds(MILLISECONDS.toMinutes((long)minutes*60000)));
    }
}
