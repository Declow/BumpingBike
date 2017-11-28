package h.group.sem.bumpingbike;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import h.group.sem.bumpingbike.Models.AccelData;
import h.group.sem.bumpingbike.Utils.DataCollectionService;

public class Biking extends AppCompatActivity {

    private Button start;
    private Button stop;
    private static String TAG = "BIKING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biking);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
    }

    public void startBiking(View v) {
        startService(new Intent(this, DataCollectionService.class));
    }

    public void stopBiking(View v) {
        Intent i = new Intent("StopDataCollection");
        sendBroadcast(i);


        //stopService(new Intent(this, DataCollectionService.class));
    }
}
