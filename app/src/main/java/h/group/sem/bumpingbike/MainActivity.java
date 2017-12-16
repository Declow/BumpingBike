package h.group.sem.bumpingbike;

import android.*;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

import java.util.ArrayList;
import java.util.List;

import h.group.sem.bumpingbike.Utils.IRec;
import h.group.sem.bumpingbike.Utils.RecReceiver;
import h.group.sem.bumpingbike.Utils.RecService;
import h.group.sem.bumpingbike.Utils.StringUtil;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , IRec {

    public static String TAG = "MainActivity";
    private PendingIntent pendingIntent;

    private GoogleApiClient googleApiClient;
    private ActivityRecognitionClient client;
    private RecReceiver receiver;
    private final int LOCATION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = ActivityRecognition.getClient(this);

        IntentFilter intf = new IntentFilter();
        intf.addAction(StringUtil.INTENT);

        receiver = new RecReceiver(this);
        this.registerReceiver(receiver, intf);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "Connected to GoogleApiClient");

        Intent intent = new Intent(this, RecService.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        client.requestActivityUpdates(3000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void command(Intent i) {
        int activityType = (int)i.getExtras().get(StringUtil.ACTIVITY_TYPE);
        int confidence = (int)i.getExtras().get(StringUtil.CONFIDENCE);
        long time = (long)i.getExtras().get(StringUtil.TIME);

        PhoneActivity pa = new PhoneActivity(activityType, confidence, time);

        TextView text = findViewById(R.id.Hello);
        text.setText(pa.toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "Connection to GoogleApiClient failed");

        Toast toast = Toast.makeText(this, "Failed connection", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this) //this is refer to connectionCallbacks interface implementation.
                .addOnConnectionFailedListener(this) //this is refer to onConnectionFailedListener interface implementation.
                .build();

        googleApiClient.connect();

        getLocationAcces();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.removeActivityUpdates(pendingIntent);
        googleApiClient.disconnect();
    }

    public void handlePosBtn(View view){
        Intent intent = new Intent(MainActivity.this, UploadPositionActivity.class);
        startActivity(intent);
    }

    public void handleTopPosBtn(View view){
        Intent intent = new Intent(MainActivity.this, TopBumpsActivity.class);
        startActivity(intent);
    }

    public void startBikingActivity(View view) {
        Intent i = new Intent(this, Biking.class);
        startActivity(i);
    }

    /**
     * Request location acces
     */
    private void getLocationAcces(){
        //Checking if the user has granted location permission for this app
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

    }
}
