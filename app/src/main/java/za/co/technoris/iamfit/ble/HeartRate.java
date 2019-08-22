package za.co.technoris.iamfit.ble;

import java.time.LocalTime;
import java.util.Locale;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static za.co.technoris.iamfit.helper.Helper.parseTime;

public class HeartRate {

    private long _date;
    private Long _id;
    private int _minute;
    private int _rate;

    public HeartRate() {
    }

    public HeartRate(Long id) {
        this._id = id;
    }

    public HeartRate(Long id, long date, int minute, int rate) {
        this._id = id;
        this._date = date;
        this._minute = minute;
        this._rate = rate;
    }

    public Long getId() {
        return this._id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public long getDate() {
        return this._date;
    }

    public void setDate(long date) {
        this._date = date;
    }

    public int getMinute() {
        return this._minute;
    }

    public void setMinute(int minute) {
        this._minute = minute;
    }

    public int getRate() {
        return this._rate;
    }

    public void setRate(int rate) {
        this._rate = rate;
    }

    public String toString() {
        return "Date: " + this._date + " Time: " + parseTime(this._minute) + " Rate: " + this._rate + " BPM\n";
    }
}
