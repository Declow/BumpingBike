package h.group.sem.bumpingbike;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Morten on 30-10-2017.
 */

public class UploadPositionActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{

    Location mLocation;
    DatabaseReference databasePositions;
    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    final int LOCATION_REQUEST_CODE = 1;
    private float zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_position);
        //Ask for location permission and enable GPS
        getLocationAcces();
        enableGPS();
        zoom = 15.0f;   //Zoom factor between 2-21
        //Get database reference
        databasePositions = FirebaseDatabase.getInstance().getReference("position");
        //Get google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sends user to enable GPS if not enabled
     */
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

    /**
     * Request location acces
     */
    private void getLocationAcces(){
        //Checking if the user has granted location permission for this app
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    Toast.makeText(this, "Location Permission enabled", Toast.LENGTH_SHORT).show();
            //Permission Granted
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    public void handleUpBtn(View view){
        addLocationToMap();
            if (mLocation != null){
                String id = databasePositions.push().getKey();
                Position pos = new Position(id, mLocation.getLatitude(), mLocation.getLongitude());

                databasePositions.child(id).setValue(pos);
                Toast.makeText(this, "Position added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No last location", Toast.LENGTH_LONG).show();
            }
    }

    public void handleShowPosBtn(View view){
       addLocationToMap();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast toast = Toast.makeText(this, "onMapReady", Toast.LENGTH_LONG);
        addLocationToMap();
    }

    private void addLocationToMap(){
        //Get location
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null){
            Toast toast = Toast.makeText(this, "No last known location...", Toast.LENGTH_LONG);
            return;
        }
        //Set zoom 
        LatLng pos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
        //Add marker to map
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mLocation.getLatitude(),
                mLocation.getLongitude())).title("You are here");
        mMap.addMarker(markerOptions); 
    }
}
