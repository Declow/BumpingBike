package h.group.sem.bumpingbike.Utils;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import h.group.sem.bumpingbike.Models.AccelData;

public class DataCollectionService extends IntentService implements SensorEventListener {

    private SensorManager sensorManager;
    private static String TAG = "DATA_COLLECTION_SERVICE";
    private ArrayList<AccelData> data;
    private DatabaseReference database;
    private DataReceiver receiver;
    private long startTime;

    public DataCollectionService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        data = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();
        receiver = new DataReceiver(this);

        IntentFilter inf = new IntentFilter();
        inf.addAction("StopDataCollection");

        registerReceiver(receiver, inf);

        startTime = System.currentTimeMillis();

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Should we do anything here?
        }
    }

    public void stopService() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String UId = mAuth.getCurrentUser().getUid();

        String id = database.child(StringUtil.ROUTE).child(UId).push().getKey();
        database.child(StringUtil.ROUTE).child(UId).child(id).setValue(data);
        onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelData d = new AccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], startTime);
        Log.v(TAG, d.toString());
        data.add(d);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }



    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        sensorManager.unregisterListener(this);
    }
}
