package h.group.sem.bumpingbike.Utils;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
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
    DatabaseReference database;

    public DataCollectionService(String name) {
        super(name);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        data = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelData d = new AccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        Log.v(TAG, d.toString());
        data.add(d);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String UId = mAuth.getCurrentUser().getUid();
        String id = database.child(StringUtil.USERS).child(UId).child(StringUtil.ROUTE).push().getKey();
        database.child(StringUtil.USERS).child(UId).child(StringUtil.ROUTE).child(id).setValue(data);
    }
}
