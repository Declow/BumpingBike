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

    private static String TAG = "Biking service";

    public RecService() {
        super("RecService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult activityRecognitionResult = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();

            int confidence = detectedActivity.getConfidence();
            int recognizeActivity = detectedActivity.getType();

            if (detectedActivity.getType() == DetectedActivity.ON_BICYCLE) {
                Log.v(TAG, "Activity: Biking");
                Log.v(TAG,"Confidence : " + confidence);
                Log.v(TAG,"RecognizeActivity : " + recognizeActivity);

                Intent notify = new Intent(StringUtil.INTENT);
                notify.putExtra("activity_type", recognizeActivity);
                notify.putExtra("confidence", confidence);
                notify.putExtra("time", activityRecognitionResult.getTime());

                sendBroadcast(notify);
            }
        }
    }
}
