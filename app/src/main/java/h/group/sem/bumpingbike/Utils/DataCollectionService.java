package h.group.sem.bumpingbike.Utils;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import h.group.sem.bumpingbike.Database.DbHelper;
import h.group.sem.bumpingbike.Database.Tables.CreateTables;
import h.group.sem.bumpingbike.Models.AccelData;
import h.group.sem.bumpingbike.Models.Position;
import h.group.sem.bumpingbike.PhoneActivity;

public class DataCollectionService extends IntentService implements SensorEventListener, LocationListener, IRec {

    private SensorManager sensorManager;
    private static String TAG = "DATA_COLLECTION_SERVICE";
    private ArrayList<AccelData> data;
    private ArrayList<Position> posData;
    private DatabaseReference database;
    private DataReceiver receiver;
    private LocationManager locationManager;
    private Location location;
    private Queue<PhoneActivity> queue;
    private boolean biking = false;
    private int activityLimit = 11;
    private int confidenceLimit = 750;
    private DbHelper helper;
    private final int THRESHOLD = 15;
    private boolean added = false;
    private long startTime;

    public DataCollectionService() {
        super(TAG);

        queue = new LinkedList<>();

    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        enableGPS();

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 1, this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        data = new ArrayList<>();
        posData = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference();
        receiver = new DataReceiver(this);

        IntentFilter inf = new IntentFilter();
        inf.addAction(StringUtil.STOP_SERVICE);

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

    /**
     * Would normally just use onDestroy but this method takes too long,
     * which means android thinks it can't shut it down.
     */
    public void stopService() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String UId = mAuth.getCurrentUser().getUid();

        String id = database.child(StringUtil.ROUTE).child(UId).push().getKey();
        database.child(StringUtil.ROUTE).child(UId).child(id).setValue(data);
        uploadPos();

        locationManager.removeUpdates(this);
        Intent i = new Intent(StringUtil.LOAD_ROUTE);
        sendBroadcast(i);

        onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelData d = new AccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], startTime);
        Log.v(TAG, d.toString());
        data.add(d);

        if (hitBump(d) && location != null && !added) {
            String id = database.child(StringUtil.POSITION).push().getKey();
            Position p = new Position(id, location.getLatitude(), location.getLongitude());
            posData.add(p);
            added = true;
        }
    }

    private void uploadPos() {
        helper = new DbHelper(this);

        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM " + CreateTables.PosData.TABLE_NAME);

        for (Position pos : posData) {
            //Upload positions
            database.child(StringUtil.POSITION).child(pos.getId()).setValue(pos);

            //Upload pos id to user
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String UId = mAuth.getCurrentUser().getUid();

            String posId = database.child(StringUtil.USERS).child(UId).child(StringUtil.POSITION).push().getKey();
            database.child(StringUtil.USERS).child(UId).child(StringUtil.POSITION).child(posId).setValue(pos.getId());

            ContentValues values = new ContentValues();
            values.put(CreateTables.PosData.COLUMN_ID, pos.getId());
            values.put(CreateTables.PosData.COLUMN_LAT, pos.getLatitude());
            values.put(CreateTables.PosData.COLUMN_LONG, pos.getLongitude());

            db.insert(CreateTables.PosData.TABLE_NAME, null, values);
        }
        db.close();
        helper.close();
    }

    private boolean hitBump(AccelData d) {
        //Hardcoded for now. Need a lot more data to come up with a formula
        double max = Math.max(Math.max(d.getX(), d.getY()), d.getZ());

        if(max > THRESHOLD)
            return true;
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        sensorManager.unregisterListener(this);
    }

    private void enableGPS(){
        try {
            int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (off == 0){
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(onGPS);
                Toast.makeText(this, "Please enable GPS to upload positions", Toast.LENGTH_SHORT).show();
            }
        } catch (Settings.SettingNotFoundException e) {
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        added = false;
        Log.v(TAG, "Location updated. Lat: " + location.getLatitude() + " \n long: " + location.getLongitude() );
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void command(Intent i) {
        int activityType = (int)i.getExtras().get(StringUtil.ACTIVITY_TYPE);
        int confidence = (int)i.getExtras().get(StringUtil.CONFIDENCE);
        long time = (long)i.getExtras().get(StringUtil.TIME);

        PhoneActivity pa = new PhoneActivity(activityType, confidence, time);
        queue.add(pa);

        //10 activities and a confidence of 750 that's 75%
        if (queue.size() > activityLimit) {
            queue.poll();
        }
        //Reset confidence
        confidence = 0;
        for (PhoneActivity p : queue) {
            confidence += p.getConfidence();
        }

        if (confidence >= confidenceLimit) {
            biking = true;
            startRoute();
        } else {
            biking = false;
            stopService();
        }
    }

    private void startRoute() {
        data = new ArrayList<>();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
}
