package h.group.sem.bumpingbike;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ditlev on 10/26/17.
 */

public class PhoneActivity {
    private int Activity;
    private int Confidence;
    private long Time;

    public PhoneActivity(int activity, int confidence, long time) {
        this.Activity = activity;
        this.Confidence = confidence;
        this.Time = time;
    }

    public int getActivity() {
        return this.Activity;
    }

    public String getActivityAsString() {
        switch (this.Activity) {
            case 1:
                return "VEHICLE";
            case 2:
                return "BICYCLE";
            case 3:
                return "STILL";
            case 4:
                return "TILTING";
            case 5:
                return "RUNNING";
            case 6:
                return "ON_FOOT";
            case 7:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }

    public int getConfidence() {
        return this.Confidence;
    }

    public long getTime() {
        return this.Time;
    }

    @Override
    public String toString() {
        Date d = new Date(this.Time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String time = Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + " " + Integer.toString(cal.get(Calendar.MINUTE));

        return this.getActivityAsString() + ", Confidence: " + this.getConfidence() + ", TimeOfDay: " + time;
    }
}
