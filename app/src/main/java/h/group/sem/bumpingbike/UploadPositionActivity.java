package h.group.sem.bumpingbike;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Morten on 30-10-2017.
 */

public class UploadPositionActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    EditText longitude;
    EditText latitude;
    Location mLocation;
    DatabaseReference databasePositions;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_position);
        setVariables();
        //Get database reference
        databasePositions = FirebaseDatabase.getInstance().getReference("position");
        //Get google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void setVariables(){
        longitude = (EditText)findViewById(R.id.longTxt);
        latitude = (EditText)findViewById(R.id.latTxt);
    }

    public void handleUpBtn(View view){
            if (mLocation != null){
                String id = databasePositions.push().getKey();
                Position pos = new Position(id, mLocation.getLongitude(), mLocation.getLatitude());

                databasePositions.child(id).setValue(pos);
                Toast.makeText(this, "Position added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No last location", Toast.LENGTH_LONG).show();
            }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Google API connected...", Toast.LENGTH_LONG).show();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
