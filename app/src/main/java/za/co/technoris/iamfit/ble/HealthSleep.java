package za.co.technoris.iamfit.ble;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class HealthSleep {
    private int _awakeCount;
    private long _dId;
    private Date _date;
    private int _day;
    private int _deepSleepCount;
    private int _deepSleepMinutes;
    private int _lightSleepCount;
    private int _lightSleepMinutes;
    private int _month;
    private Long _sleepDataId;
    private int _sleepEndedTimeH;
    private int _sleepEndedTimeM;
    private int _totalSleepMinutes;
    private int _year;

    public HealthSleep(Long sleepDataId, long dId, int year, int month, int day,
                       int sleepEndedTimeH, int sleepEndedTimeM, int totalSleepMinutes,
                       int lightSleepCount, int deepSleepCount, int awakeCount,
                       int lightSleepMinutes, int deepSleepMinutes, Date date) {
        this._sleepDataId = sleepDataId;
        this._dId = dId;
        this._year = year;
        this._month = month;
        this._day = day;
        this._sleepEndedTimeH = sleepEndedTimeH;
        this._sleepEndedTimeM = sleepEndedTimeM;
        this._totalSleepMinutes = totalSleepMinutes;
        this._lightSleepCount = lightSleepCount;
        this._deepSleepCount = deepSleepCount;
        this._awakeCount = awakeCount;
        this._lightSleepMinutes = lightSleepMinutes;
        this._deepSleepMinutes = deepSleepMinutes;
        this._date = date;
    }

    public HealthSleep() {
    }

    public Long getSleepDataId() {
        return this._sleepDataId;
    }

    public void setSleepDataId(Long sleepDataId) {
        this._sleepDataId = sleepDataId;
    }

    public long getDId() {
        return this._dId;
    }

    public void setDId(long dId) {
        this._dId = dId;
    }

    public int getYear() {
        return this._year;
    }

    public void setYear(int year) {
        this._year = year;
    }

    public int getMonth() {
        return this._month;
    }

    public void setMonth(int month) {
        this._month = month;
    }

    public int getDay() {
        return this._day;
    }

    public void setDay(int day) {
        this._day = day;
    }

    public int getSleepEndedTimeH() {
        return this._sleepEndedTimeH;
    }

    public void setSleepEndedTimeH(int sleepEndedTimeH) {
        this._sleepEndedTimeH = sleepEndedTimeH;
    }

    public int getSleepEndedTimeM() {
        return this._sleepEndedTimeM;
    }

    public void setSleepEndedTimeM(int sleepEndedTimeM) {
        this._sleepEndedTimeM = sleepEndedTimeM;
    }

    public int getTotalSleepMinutes() {
        return this._totalSleepMinutes;
    }

    public void setTotalSleepMinutes(int totalSleepMinutes) {
        this._totalSleepMinutes = totalSleepMinutes;
    }

    public int getLightSleepCount() {
        return this._lightSleepCount;
    }

    public void setLightSleepCount(int lightSleepCount) {
        this._lightSleepCount = lightSleepCount;
    }

    public int getDeepSleepCount() {
        return this._deepSleepCount;
    }

    public void setDeepSleepCount(int deepSleepCount) {
        this._deepSleepCount = deepSleepCount;
    }

    public int getAwakeCount() {
        return this._awakeCount;
    }

    public void setAwakeCount(int awakeCount) {
        this._awakeCount = awakeCount;
    }

    public int getLightSleepMinutes() {
        return this._lightSleepMinutes;
    }

    public void setLightSleepMinutes(int lightSleepMinutes) {
        this._lightSleepMinutes = lightSleepMinutes;
    }

    public int getDeepSleepMinutes() {
        return this._deepSleepMinutes;
    }

    public void setDeepSleepMinutes(int deepSleepMinutes) {
        this._deepSleepMinutes = deepSleepMinutes;
    }

    public Date getDate() {
        return this._date;
    }

    public void setDate(Date date) {
        this._date = date;
    }

    public String toString() {
        return "Date: " + this._day + "/" + this._month + "/" + this._year + " End Time: " + this._sleepEndedTimeH + ":" + this._sleepEndedTimeM + " Sleep Minutes: " + this._totalSleepMinutes + '\n';
    }

}
