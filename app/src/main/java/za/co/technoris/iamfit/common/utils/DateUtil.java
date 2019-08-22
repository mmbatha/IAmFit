package za.co.technoris.iamfit.common.utils;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import za.co.technoris.iamfit.common.logger.Log;

public class DateUtil {

    public static final String TRACE_TODAY_VISIT_SPLIT = ":";
    private static final String EVENT_PARAM_VALUE_NO = "0";
    private static final String DATE_FORMAT_YMDHm = "yyyy/MM/dd HH:mm";
    private static final long DAY = 86400000;
    private static final long HOUR = 3600000;
    public static final long MINUTE = 60000;
    public static final int UNIT_DAY = 0;
    public static final int UNIT_HOUR = 1;
    public static final int UNIT_MINUTE = 2;
    public static final String DEFAULT_OPT_PREFIX = "-";
    private static Calendar calendar = null;
    private static final Locale enZA = new Locale("en", "ZA");
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", enZA);
    private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM-dd", enZA);

    public static String getFormatDate2() {
        return simpleDateFormat2.format(new Date());
    }

    public static long getSportStartTime(int hour, int min, int second) {
        String date = new SimpleDateFormat("yyyyMMdd", enZA).format(new Date());
        String stringBuffer = date +
                String.format(enZA, "%02d", hour) +
                String.format(enZA, "%02d", min) +
                String.format(enZA, "%02d", second);
        return Long.parseLong(stringBuffer);
    }

    public static Date getDate(int year, int month, int day) {
        return new Date(year - 1900, month - 1, day, 0, 0, 0);
    }

    public static String getFormatDate() {
        return new SimpleDateFormat("yyyy/MM/dd", enZA).format(new Date());
    }

    public static String getYearAndMouth() {
        return new SimpleDateFormat("yyyy year M month", enZA).format(new Date());
    }

    public static String getMouthAndDay() {
        return new SimpleDateFormat("M month d", enZA).format(new Date());
    }

//    public static String computerDate() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", enZA);
//        Date date = null;
//        Date now = null;
//        try {
//            try {
//                date = sdf.parse("2015-11-22");
//                now = sdf.parse(sdf.format(new Date()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return String.valueOf((now.getTime() - date.getTime()) / DAY);
//        }
//        catch (NullPointerException ex) {
//            Log.e("Error", ex.getMessage());
//        }
//    }

    public static String computeTime(long time) {
        int hour = (int) ((time / 60) / 60);
        StringBuffer result = new StringBuffer();
        if (hour > 0) {
            if (hour < 10) {
                result.append(EVENT_PARAM_VALUE_NO);
            }
            time -= (long) ((hour * 60) * 60);
            result.append(hour + TRACE_TODAY_VISIT_SPLIT);
        }
        int minute = (int) (time / 60);
        if (minute < 10) {
            result.append(EVENT_PARAM_VALUE_NO);
        }
        int second = (int) (time - ((long) (minute * 60)));
        result.append(minute + TRACE_TODAY_VISIT_SPLIT);
        if (second < 10) {
            result.append(EVENT_PARAM_VALUE_NO);
        }
        result.append(second);
        return result.toString();
    }

    public static String computeTimeMS(int time) {
        int minute = time / 60;
        int second = time % 60;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(enZA, "%02d", minute));
        sb.append("'");
        sb.append(String.format(enZA, "%02d", second));
        sb.append("''");
        return sb.toString();
    }

//    public static String computeTimePace(String pace) {
//        if (pace.contains(FileUtil.HIDDEN_PREFIX)) {
//            String[] paces = pace.split("\\.");
//            return String.format("%02d", Integer.valueOf(Integer.parseInt(paces[0]))) + "'" + String.format("%02d", Integer.valueOf(Integer.parseInt(paces[1]))) + "''";
//        } else if (pace.contains(",")) {
//            String[] paces2 = pace.split(",");
//            return String.format("%02d", Integer.valueOf(Integer.parseInt(paces2[0]))) + "'" + String.format("%02d", Integer.valueOf(Integer.parseInt(paces2[1]))) + "''";
//        } else {
//            return String.format("%02d", Integer.valueOf(0)) + "'" + String.format("%02d", Integer.valueOf(0)) + "''";
//        }
//    }

    public static String computeTimeHMS(long time) {
        int hour = (int) ((time / 60) / 60);
        StringBuffer result = new StringBuffer();
        long time2 = time - ((long) ((hour * 60) * 60));
        result.append(String.format(enZA, "%02d", hour));
        result.append(TRACE_TODAY_VISIT_SPLIT);
        int minute = (int) (time2 / 60);
        int second = (int) (time2 - ((long) (minute * 60)));
        result.append(String.format(enZA, "%02d", minute));
        result.append(TRACE_TODAY_VISIT_SPLIT);
        result.append(String.format(enZA, "%02d", second));
        return result.toString();
    }

    public static String format2(int year, int mouth, int day) {
        return year + "/" + String.format(enZA, "%02d", new Object[]{mouth}) + "/" + String.format(enZA, "%02d", new Object[]{day});
    }

    public static String format3(int year, int mouth, int day) {
        return "" + year + String.format(enZA, "%02d", mouth) + String.format(enZA, "%02d", day);
    }

    public static String format(int year, int mouth, int day) {
        return year + DEFAULT_OPT_PREFIX + String.format(enZA, "%02d", new Object[]{mouth}) + DEFAULT_OPT_PREFIX + String.format(enZA, "%02d", new Object[]{day});
    }

    public static String format(int month) {
        return String.format(enZA, "%02d", month);
    }

    public static String format(long time) {
        return simpleDateFormat.format(new Date(time));
    }

    public static String format(SimpleDateFormat dateFormat, int year, int month, int day) {
        return dateFormat.format(new Date(year, month, day));
    }

    public static String format(SimpleDateFormat dateFormat, Date date) {
        return dateFormat.format(date);
    }

    public static String formatAdjustDate(SimpleDateFormat dateFormat, Date date) {
        Date adjustDate = (Date) date.clone();
        adjustDate.setYear(date.getYear() - 1900);
        return dateFormat.format(adjustDate);
    }

    public static boolean isToday(int year, int month, int day) {
        return simpleDateFormat2.format(new Date()).equals(format(year, month, day));
    }

    public static int[] todayYearMonthDay() {
        calendar = Calendar.getInstance();
        return new int[]{calendar.get(1), calendar.get(2) + 1, calendar.get(5)};
    }

    public static int getTodayHour() {
        return Calendar.getInstance().get(11);
    }

    public static int getTodayMin() {
        return Calendar.getInstance().get(12);
    }

    public static int[] yearMonthDay(String dateStr) {
        String[] s = dateStr.split(DEFAULT_OPT_PREFIX);
        int[] date = new int[3];
        int i = 0;
        while (i < s.length && i < date.length) {
            date[i] = Integer.parseInt(s[i]);
            i++;
        }
        return date;
    }

    public static String getUpLoadServiceDate(int year, int month, int day) {
        return String.format(enZA, "%04d", year) + DEFAULT_OPT_PREFIX + String.format(enZA, "%02d", month) + DEFAULT_OPT_PREFIX + String.format(enZA, "%02d", day) + " 00:00:00";
    }

    public static boolean isInDate(Date date, String strDateBegin, String strDateEnd) {
        String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(date);
        int tempDate = Integer.parseInt(strDate.substring(11, 13) + strDate.substring(14, 16) + strDate.substring(17, 19));
        int tempDateBegin = Integer.parseInt(strDateBegin.substring(0, 2) + strDateBegin.substring(3, 5) + strDateBegin.substring(6, 8));
        int tempDateEnd = Integer.parseInt(strDateEnd.substring(0, 2) + strDateEnd.substring(3, 5) + strDateEnd.substring(6, 8));
        return tempDate >= tempDateBegin && tempDate <= tempDateEnd;
    }

    public static String getDay() {
        calendar = Calendar.getInstance();
        StringBuffer buffer = new StringBuffer();
        int year = calendar.get(1);
        if (year < 10) {
            buffer.append(EVENT_PARAM_VALUE_NO).append(year);
        } else {
            buffer.append(year);
        }
        buffer.append(DEFAULT_OPT_PREFIX);
        int month = calendar.get(2) + 1;
        if (month < 10) {
            buffer.append(EVENT_PARAM_VALUE_NO).append(month);
        } else {
            buffer.append(month);
        }
        buffer.append(DEFAULT_OPT_PREFIX);
        int day = calendar.get(5);
        if (day < 10) {
            buffer.append(EVENT_PARAM_VALUE_NO).append(day);
        } else {
            buffer.append(day);
        }
        return new String(buffer);
    }

    public static long getLongFromDateStr(String currentTimeMillis) {
        try {
            return simpleDateFormat.parse(currentTimeMillis).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Date getDateByHMS(int hour, int minute, int second) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(11, hour);
        calendar2.set(12, minute);
        calendar2.set(13, second);
        calendar2.set(14, 0);
        return calendar2.getTime();
    }

    public static int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", enZA);
        long between_days = 0;
        try {
            Date smdate2 = sdf.parse(sdf.format(smdate));
            Date bdate2 = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate2);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate2);
            between_days = (cal.getTimeInMillis() - time1) / DAY;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(String.valueOf(between_days));
    }

    private static String getSpecifiedDayBefore(String specifiedDay, int days) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_FORMAT_YMDHm, enZA).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        c.set(5, c.get(5) - days);
        return new SimpleDateFormat(DATE_FORMAT_YMDHm, enZA).format(c.getTime());
    }

    public static boolean isExpire(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMDHm, enZA);
        try {
            if (sdf.parse(sdf.format(new Date())).before(date)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Date string2Date(String dateString, String style) {
        Date date = new Date();
        try {
            return new SimpleDateFormat(style, enZA).parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    public static int[] getCurrentDate() {
        Calendar calendar2 = Calendar.getInstance();
        return new int[]{calendar2.get(1), calendar2.get(2) + 1, calendar2.get(5)};
    }

    public static String dateFormat(int year, int month, int day) {
        return year + "/" + numberFormat(month) + "/" + numberFormat(day);
    }

    private static String numberFormat(int number) {
        if (number >= 10 || number < 0) {
            return String.valueOf(number);
        }
        return EVENT_PARAM_VALUE_NO + number;
    }

    private static long getTimeInterval(String dateStr, int cycleLength, String dateFormat) {
        Calendar calendar2 = Calendar.getInstance();
        long currentTimeMillis = calendar2.getTimeInMillis();
        try {
            calendar2.setTime(new SimpleDateFormat(dateFormat, enZA).parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long interval = calendar2.getTimeInMillis() - currentTimeMillis;
        while (interval <= 0) {
            dateStr = getSpecifiedDayBefore(dateStr, -cycleLength);
            getTimeInterval(dateStr, cycleLength, dateFormat);
        }
        return interval;
    }

//    public static String getDateFormatMd(String dateStr, String separator) {
//        String[] strings = dateStr.split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR)[0].split(separator);
//        if (strings.length >= 3) {
//            return strings[1] + separator + strings[2];
//        }
//        return dateStr;
//    }

    public static int[] getIntervalTypeAndValue(long timeInterval) {
        int[] ints = new int[2];
        if (timeInterval / DAY > 0) {
            ints[0] = 0;
            ints[1] = (int) (timeInterval / DAY);
        } else if (timeInterval / HOUR > 0) {
            ints[0] = 1;
            ints[1] = (int) (timeInterval / HOUR);
        } else {
            ints[0] = 2;
            ints[1] = (int) (timeInterval / 60000);
        }
        return ints;
    }
}
