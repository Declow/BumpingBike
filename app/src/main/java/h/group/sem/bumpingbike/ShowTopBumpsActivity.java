package h.group.sem.bumpingbike;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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

import java.util.ArrayList;

import h.group.sem.bumpingbike.Models.Position;

/**
 * Created by Morten on 19-11-2017.
 */

public class ShowTopBumpsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{

    ArrayList<Position> locations;
    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    private float zoomFactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_top_bumps);

        zoomFactor = 9.0f;
        locations = (ArrayList<Position>) getIntent().getSerializableExtra("positions");
        //Get google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.showBumpsMap);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addLocationsToMap();
    }

    /**
     * Adds a marker to the map for each position passed in the intent
     */
    private void addLocationsToMap() {
        LatLng pos = null;
        MarkerOptions markerOption = null;
        for (Position p : locations){
            pos = new LatLng(p.getLatitude(), p.getLongitude());
            markerOption = new MarkerOptions().position(pos).title("Bump");
            mMap.addMarker(markerOption);
        }
        //Set zoom to last position, if any
        if (pos != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoomFactor));
        }
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
