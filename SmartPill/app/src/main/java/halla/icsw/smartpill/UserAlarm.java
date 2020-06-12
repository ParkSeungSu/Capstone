package halla.icsw.smartpill;

import android.widget.TimePicker;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserAlarm  {
    private String id;
    private String madicine;
    private String clock;
    private long longTime;
    public UserAlarm(){

    }

    public UserAlarm(String id, String madicine, String clock, long longTime) {
        this.id = id;
        this.madicine = madicine;
        this.clock = clock;
        this.longTime=longTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMadicine() {
        return madicine;
    }

    public void setMadicine(String madicine) {
        this.madicine = madicine;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public long getLongTime() {
        return longTime;
    }

    public void setLongTime(long longTime) {
        this.longTime = longTime;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("clock", clock);
        result.put("madicine", madicine);
        result.put("id", id);
        result.put("longTime",longTime);

        return result;

    }
}
