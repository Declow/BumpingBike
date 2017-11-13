package h.group.sem.bumpingbike;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import static java.lang.Math.toRadians;

// CustomAdapter
// https://stackoverflow.com/questions/15832335/android-custom-row-item-for-listview

public class TopBumpsActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Location mLocation;
    DatabaseReference databasePositions;
    GoogleApiClient mGoogleApiClient;
    private ProgressBar progressBar;
    View topBumpsView;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_bumps);
        // getting handle of list
        final ListView list = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        progressBar.setVisibility(View.VISIBLE);

        //Get google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // defining Adapter for List content
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        //Get database reference
        databasePositions = FirebaseDatabase.getInstance().getReference("position");

        databasePositions.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        collectPositions((Map<String,Object>) dataSnapshot.getValue(), adapter);
                        System.out.println("Finished fetching locations");
                        progressBar.setVisibility(View.GONE);
                        // setting adapter to list
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        // setting action listen on list item
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TextView sel = (TextView) arg1;
                String selectedItem = sel.getText().toString();
/*
                new AlertDialog.Builder(StrListActivity.this)
                        .setTitle("Selection Information")
                        .setMessage("You have selected " + selectedItem)
                        .setNeutralButton("OK",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();
                        */

            }
        });
    }

    private void collectPositions(Map<String,Object> users, ArrayAdapter<String> adapter) {

        ArrayList<Position> locations = new ArrayList<>();


        for (Map.Entry<String, Object> entry : users.entrySet()){

            Map singlePosition = (Map) entry.getValue();

            String id = singlePosition.get("id").toString();
            double latitude = new Double(singlePosition.get("latitude").toString());
            double longitude = new Double(singlePosition.get("longitude").toString());

            Position position = new Position( id, latitude, longitude );

            for (int i = 0; i<locations.size();i++) {
                Position currentPos = locations.get(i);
                if(currentPos.getId()==position.getId())
                    continue;
                double dist = calculateDistance(position.latitude, position.longitude, currentPos.latitude, currentPos.longitude);
            }

            locations.add(position);
        }

        for (Position position: locations) {
            String value = "";
            value += "Latitude: " + position.latitude;
            value += "\n";
            value += "Longitude: " + position.longitude;
            value += "\n";
            adapter.add(value);
        }

        System.out.println(locations.toString());
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // https://www.movable-type.co.uk/scripts/latlong.html

        double R = 6371e3; // metres

        double phi1 = toRadians(lat1);
        double phi2 = toRadians(lat2);
        double delta_phi = toRadians(lat2-lat1);
        double delta_lambda = toRadians(lon2-lon1);

        double a = Math.sin(delta_phi/2) * Math.sin(delta_phi/2) +
                Math.cos(phi1) * Math.cos(phi2) * Math.sin(delta_lambda/2) * Math.sin(delta_lambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
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


/*

public class TopBumpsActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_bumps);

        topBumpsView = getLayoutInflater().inflate(R.layout.activity_top_bumps, null);

        spinner = (ProgressBar)topBumpsView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);



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






}
*/
