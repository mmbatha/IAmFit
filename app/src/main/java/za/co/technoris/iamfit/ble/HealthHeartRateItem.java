package za.co.technoris.iamfit.ble;

import java.util.Date;

public class HealthHeartRateItem {
    private int _heartRateValue;
    private long _dId;
    private Date _date;
    private int _day;
    private Long _heartRateDataId;
    private int _month;
    private int _offsetMinute;
    private int _year;

    public HealthHeartRateItem(Long heartRateDataId, long dId, int year, int month, int day,
                               int offsetMinute, int heartRateValue, Date date) {
        this._heartRateDataId = heartRateDataId;
        this._dId = dId;
        this._year = year;
        this._month = month;
        this._day = day;
        this._offsetMinute = offsetMinute;
        this._heartRateValue = heartRateValue;
        this._date = date;
    }

    public HealthHeartRateItem() {
    }

    public Long getHeartRateDataId() {
        return this._heartRateDataId;
    }

    public void setHeartRateDataId(Long heartRateDataId) {
        this._heartRateDataId = heartRateDataId;
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

    public int getOffsetMinute() {
        return this._offsetMinute;
    }

    public void setOffsetMinute(int offsetMinute) {
        this._offsetMinute = offsetMinute;
    }

    public int getHeartRateValue() {
        return this._heartRateValue;
    }

    public void setHeartRateValue(int heartRateValue) {
        this._heartRateValue = heartRateValue;
    }

    public Date getDate() {
        return this._date;
    }

    public void setDate(Date date) {
        this._date = date;
    }

    public String toString() {
        return "Date: " + this._day + "/" + this._month + "/" + this._year + " Rate: " + this._heartRateValue + " BPM\n";
    }
}
