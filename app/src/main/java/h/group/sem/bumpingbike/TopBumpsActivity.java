package h.group.sem.bumpingbike;

import android.app.ActionBar;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class TopBumpsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Location mLocation;
    DatabaseReference databasePositions;
    GoogleApiClient mGoogleApiClient;
    private ProgressBar spinner;
    View topBumpsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_bumps);

        topBumpsView = getLayoutInflater().inflate(R.layout.activity_top_bumps, null);

        spinner = (ProgressBar)topBumpsView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        //Get google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Get database reference
        databasePositions = FirebaseDatabase.getInstance().getReference("position");

        databasePositions.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        collectPositions((Map<String,Object>) dataSnapshot.getValue());
                        System.out.println("Finished fetching locations");
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    private void collectPositions(Map<String,Object> users) {

        ArrayList<Position> locations = new ArrayList<>();
        ArrayList<String> stringLocations = new ArrayList<>();

        for (Map.Entry<String, Object> entry : users.entrySet()){

            Map singlePosition = (Map) entry.getValue();

            String id = singlePosition.get("id").toString();
            double latitude = new Double(singlePosition.get("latitude").toString());
            double longitude = new Double(singlePosition.get("longitude").toString());

            locations.add(new Position( id, latitude, longitude ));
            stringLocations.add("Latitude: " + latitude + " - Longitude: " + longitude);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, stringLocations);

        ListView listView = (ListView) topBumpsView.findViewById(R.id.topPositions);
        listView.setAdapter(adapter);

        System.out.println(locations.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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