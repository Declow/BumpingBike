package h.group.sem.bumpingbike;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RecService extends IntentService {

    public RecService() {
        super("RecService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult activityRecognitionResult = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();

            int confidence = detectedActivity.getConfidence();
            int recognizeActivity = getActivityName(detectedActivity.getType());

            Log.v("Log","Confidence : " + confidence);
            Log.v("Log","RecognizeActivity : " + recognizeActivity);

            Intent notify = new Intent(StringUtil.INTENT);
            notify.putExtra("activity_type", recognizeActivity);
            notify.putExtra("confidence", confidence);
            notify.putExtra("time", activityRecognitionResult.getTime());

            sendBroadcast(notify);
        }
    }

    private int getActivityName(int activityType){
        switch (activityType){
            case DetectedActivity.IN_VEHICLE:
                return 1;
            case DetectedActivity.ON_BICYCLE:
                return 2;
            case DetectedActivity.STILL:
                return 3;
            case DetectedActivity.TILTING:
                return 4;
            case DetectedActivity.RUNNING:
                return 5;
            case DetectedActivity.ON_FOOT:
                return 6;
            case DetectedActivity.WALKING:
                return 7;
            case DetectedActivity.UNKNOWN:
                return 8;
        }
        return 0;
    }
}
