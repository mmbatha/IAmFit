package za.co.technoris.iamfit.ble;

import android.os.Parcel;
import android.os.Parcelable;

public class SportDataDay implements Parcelable {
    public static final Creator<SportDataDay> CREATOR = new Creator<SportDataDay>() {
        public SportDataDay createFromParcel(Parcel source) {
            SportDataDay data = new SportDataDay();
            data._date = source.readLong();
            data._totalStepCount = source.readInt();
            data._totalCalories = source.readInt();
            data._totalDistance = source.readInt();
            data._totalActiveTime = source.readInt();
            return data;
        }

        public SportDataDay[] newArray(int size) {
            return new SportDataDay[size];
        }
    };
    /* access modifiers changed from: private */
    private Long _date;
    /* access modifiers changed from: private */
    private int _totalActiveTime;
    /* access modifiers changed from: private */
    private int _totalCalories;
    /* access modifiers changed from: private */
    private int _totalDistance;
    /* access modifiers changed from: private */
    private int _totalStepCount;

    public SportDataDay() {
    }

    public SportDataDay(Long date) {
        this._date = date;
    }

    public SportDataDay(Long date, int totalStepCount, int totalCalories, int totalDistance,
                        int totalActiveTime) {
        this._date = date;
        this._totalStepCount = totalStepCount;
        this._totalCalories = totalCalories;
        this._totalDistance = totalDistance;
        this._totalActiveTime = totalActiveTime;
    }

    public Long getDate() {
        return this._date;
    }

    public void setDate(Long date) {
        this._date = date;
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._date);
        dest.writeInt(this._totalStepCount);
        dest.writeInt(this._totalCalories);
        dest.writeInt(this._totalDistance);
        dest.writeInt(this._totalActiveTime);
    }

    public String toString() {
        return "Date: " + this._date + " Steps: " + this._totalStepCount + '\n';
    }
}
