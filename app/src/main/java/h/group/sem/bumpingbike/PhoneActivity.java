package h.group.sem.bumpingbike;

import com.google.android.gms.location.DetectedActivity;

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
            case DetectedActivity.IN_VEHICLE:
                return "VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "BICYCLE";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.WALKING:
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
