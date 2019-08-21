package za.co.technoris.iamfit.ble;

import android.os.Parcel;
import android.os.Parcelable;

public class SleepDataDay implements Parcelable {
    public static final Creator<SleepDataDay> CREATOR = new Creator<SleepDataDay>() {
        public SleepDataDay createFromParcel(Parcel source) {
            SleepDataDay data = new SleepDataDay();
            data._date = source.readLong();
            data._endTimeHour = source.readInt();
            data._endTimeMinute = source.readInt();
            data._totalSleepMinutes = source.readInt();
            data._lightSleepCount = source.readInt();
            data._deepSleepCount = source.readInt();
            data._awakeCount = source.readInt();
            data._lightSleepMinutes = source.readInt();
            data._deepSleepMinutes = source.readInt();
            return data;
        }

        public SleepDataDay[] newArray(int size) {
            return new SleepDataDay[size];
        }
    };
    /* access modifiers changed from: private */
    private int _awakeCount;
    /* access modifiers changed from: private */
    private Long _date;
    /* access modifiers changed from: private */
    private int _deepSleepCount;
    /* access modifiers changed from: private */
    private int _deepSleepMinutes;
    /* access modifiers changed from: private */
    private int _endTimeHour;
    /* access modifiers changed from: private */
    private int _endTimeMinute;
    /* access modifiers changed from: private */
    private int _lightSleepCount;
    /* access modifiers changed from: private */
    private int _lightSleepMinutes;
    /* access modifiers changed from: private */
    private int _totalSleepMinutes;

    public SleepDataDay() {
    }

    public SleepDataDay(Long date) {
        this._date = date;
    }

    public SleepDataDay(Long date, int endTimeHour, int endTimeMinute, int totalSleepMinutes,
                        int lightSleepCount, int deepSleepCount, int awakeCount,
                        int lightSleepMinutes, int deepSleepMinutes) {
        this._date = date;
        this._endTimeHour = endTimeHour;
        this._endTimeMinute = endTimeMinute;
        this._totalSleepMinutes = totalSleepMinutes;
        this._lightSleepCount = lightSleepCount;
        this._deepSleepCount = deepSleepCount;
        this._awakeCount = awakeCount;
        this._lightSleepMinutes = lightSleepMinutes;
        this._deepSleepMinutes = deepSleepMinutes;
    }

    public Long getDate() {
        return this._date;
    }

    public void setDate(Long date) {
        this._date = date;
    }

    public int getEndTimeHour() {
        return this._endTimeHour;
    }

    public void setEndTimeHour(int endTimeHour) {
        this._endTimeHour = endTimeHour;
    }

    public int getEndTimeMinute() {
        return this._endTimeMinute;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this._endTimeMinute = endTimeMinute;
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._date);
        dest.writeInt(this._endTimeHour);
        dest.writeInt(this._endTimeMinute);
        dest.writeInt(this._totalSleepMinutes);
        dest.writeInt(this._lightSleepCount);
        dest.writeInt(this._deepSleepCount);
        dest.writeInt(this._awakeCount);
        dest.writeInt(this._lightSleepMinutes);
        dest.writeInt(this._deepSleepMinutes);
    }

    public String toString() {
        return "Date: " + this._date + " End Time: " + this._endTimeHour + ":" + this._endTimeMinute + " Sleep Minutes: " + this._totalSleepMinutes + '\n';
    }
}
