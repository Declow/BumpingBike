package h.group.sem.bumpingbike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import h.group.sem.bumpingbike.Database.DbHelper;
import h.group.sem.bumpingbike.Models.Position;
import h.group.sem.bumpingbike.Utils.DataCollectionService;
import h.group.sem.bumpingbike.Utils.IRec;
import h.group.sem.bumpingbike.Utils.StringUtil;

public class Biking extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks
        ,OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener
        , IRec {

    private Button start;
    private Button stop;
    private static String TAG = "BIKING";
    private float zoom;
    private GoogleMap mMap;
    private BReceiver receiver;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biking);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        receiver = new BReceiver(this);

        IntentFilter intf = new IntentFilter();
        intf.addAction(StringUtil.LOAD_ROUTE);

        this.registerReceiver(receiver, intf);
        zoom = 15.0f;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void startBiking(View v) {
        startService(new Intent(this, DataCollectionService.class));
    }

    public void stopBiking(View v) {
        Intent i = new Intent("StopDataCollection");
        sendBroadcast(i);


        //stopService(new Intent(this, DataCollectionService.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.v(TAG, "Map ready");
    }

    @Override
    public void command(Intent i) {
            DbHelper db = new DbHelper(this);
            List<Position> l = db.readDb();

            for (Position p : l) {
                LatLng pos = new LatLng(p.getLatitude(), p.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
                //Add marker to map
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(p.getLatitude(),
                        p.getLongitude()));
                mMap.addMarker(markerOptions);
            }
            Toast.makeText(this, "Positions: " + l.size(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.myMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast toast = Toast.makeText(this, "Failed connection", Toast.LENGTH_LONG);
        toast.show();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}

class BReceiver extends BroadcastReceiver {

    private IRec rec;
    public BReceiver(IRec i) {
        super();
        this.rec = i;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        rec.command(intent);
    }
}
