package halla.icsw.smartpill;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ParcelCreator")
public class DatePill implements Parcelable {
    static int num;
    static String date;
    static String day;

    public DatePill() {
    }
    public DatePill(int num,String date,String day) {
        this.num=num;
        this.date=date;
        this.day=day;
    }

    protected DatePill(Parcel in) {
    }

    public static final Creator<DatePill> CREATOR = new Creator<DatePill>() {
        @Override
        public DatePill createFromParcel(Parcel in) {
            return new DatePill(in);
        }

        @Override
        public DatePill[] newArray(int size) {
            return new DatePill[size];
        }
    };

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        DatePill.num = num;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        DatePill.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        DatePill.day = day;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("num", num);
        result.put("date", date);
        result.put("day", day);

        return result;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.num);
        dest.writeString(this.date);
        dest.writeString(this.day);
    }
}
