package za.co.technoris.iamfit.ble;

import static za.co.technoris.iamfit.helper.Helper.parseTime;

public class SportDataItem {
    private long _date;
    private int _hour;
    private int _minute;
    private int _stepCount;

    public SportDataItem(){

    }

    public long getDate() {
        return _date;
    }

    public void setDate(long _date) {
        this._date = _date;
    }

    public int getHour() {
        return _hour;
    }

    public void setHour(int _hour) {
        this._hour = _hour;
    }

    public int getMinute() {
        return _minute;
    }

    public void setMinute(int _minute) {
        this._minute = _minute;
    }

    public int getStepCount() {
        return _stepCount;
    }

    public void setStepCount(int _stepCount) {
        this._stepCount = _stepCount;
    }

    public String toString() {
        return "Time: " + parseTime(this._hour, this._minute) + " Steps: " + this._stepCount;
    }
}
