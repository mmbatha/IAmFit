package za.co.technoris.iamfit.ble;

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
        return "Time: " + parseTime(this._minute) + " Heart rate: " + this._rate + " BPM";
    }
}
