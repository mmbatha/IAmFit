package za.co.technoris.iamfit.ble;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class HealthSport {
    private long _dId;
    private Date _date;
    private int _day;
    private int _month;
    private Long _sportDataId;
    private int _startTime;
    private int _timeSpace;
    private int _totalActiveTime;
    private int _totalCalories;
    private int _totalDistance;
    private int _totalStepCount;
    private int _year;

    public HealthSport(Long sportDataId, long dId, int year, int month, int day, int totalStepCount,
                       int totalCalories, int totalDistance, int totalActiveTime, int startTime,
                       int timeSpace, Date date) {
        this._sportDataId = sportDataId;
        this._dId = dId;
        this._year = year;
        this._month = month;
        this._day = day;
        this._totalStepCount = totalStepCount;
        this._totalCalories = totalCalories;
        this._totalDistance = totalDistance;
        this._totalActiveTime = totalActiveTime;
        this._startTime = startTime;
        this._timeSpace = timeSpace;
        this._date = date;
    }

    public HealthSport() {
    }

    public Long getSportDataId() {
        return this._sportDataId;
    }

    public void setSportDataId(Long sportDataId) {
        this._sportDataId = sportDataId;
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

    public int getTotalStepCount() {
        return this._totalStepCount;
    }

    public void setTotalStepCount(int totalStepCount) {
        this._totalStepCount = totalStepCount;
    }

    public int getTotalCalories() {
        return this._totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this._totalCalories = totalCalories;
    }

    public int getTotalDistance() {
        return this._totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this._totalDistance = totalDistance;
    }

    public int getTotalActiveTime() {
        return this._totalActiveTime;
    }

    public void setTotalActiveTime(int totalActiveTime) {
        this._totalActiveTime = totalActiveTime;
    }

    public int getStartTime() {
        return this._startTime;
    }

    public void setStartTime(int startTime) {
        this._startTime = startTime;
    }

    public int getTimeSpace() {
        return this._timeSpace;
    }

    public void setTimeSpace(int timeSpace) {
        this._timeSpace = timeSpace;
    }

    public Date getDate() {
        return this._date;
    }

    public void setDate(Date date) {
        this._date = date;
    }

    public String toString() {
        return "Date: " + this._day + '/' + this._month + '/' + this._year + " Steps: " + this._totalStepCount + '\n';
    }

}
