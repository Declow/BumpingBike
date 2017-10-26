package h.group.sem.bumpingbike;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , IRec {

    public static String TAG = "MainActivity";
    private PendingIntent pendingIntent;

    private GoogleApiClient googleApiClient;
    private ActivityRecognitionClient client;
    private RecReceiver receiver;
    private List<PhoneActivity> activites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = ActivityRecognition.getClient(this);
        Intent intent = new Intent( this, RecService.class );
        PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        IntentFilter intf = new IntentFilter();
        intf.addAction(StringUtil.INTENT);

        receiver = new RecReceiver(this);
        this.registerReceiver(receiver, intf);
        activites = new ArrayList<>();

        start();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "Connected to GoogleApiClient");

        client.requestActivityUpdates(30000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void InsertActivity(Intent i) {
        int activityType = (int)i.getExtras().get(StringUtil.ACTIVITY_TYPE);
        int confidence = (int)i.getExtras().get(StringUtil.CONFIDENCE);
        long time = (long)i.getExtras().get(StringUtil.TIME);

        activites.add(new PhoneActivity(activityType, confidence, time));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "Connection to GoogleApiClient failed");

        Toast toast = Toast.makeText(this, "Failed connection", Toast.LENGTH_LONG);
        toast.show();
    }

    private void start() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this) //this is refer to connectionCallbacks interface implementation.
                .addOnConnectionFailedListener(this) //this is refer to onConnectionFailedListener interface implementation.
                .build();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.removeActivityUpdates(pendingIntent);
        googleApiClient.disconnect();
    }
}
