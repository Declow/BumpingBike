package h.group.sem.bumpingbike;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Morten on 30-10-2017.
 */

public class UploadPositionActivity extends AppCompatActivity {

    EditText longitude;
    EditText latitude;
    DatabaseReference databasePositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_position);
        setVariables();
        //Get database reference
        databasePositions = FirebaseDatabase.getInstance().getReference("position");
    }

    public void setVariables(){
        longitude = (EditText)findViewById(R.id.longTxt);
        latitude = (EditText)findViewById(R.id.latTxt);
    }

    public void handleUpBtn(View view){
        double longitude = Double.parseDouble(this.longitude.getText().toString().trim());
        double latitude = Double.parseDouble(this.latitude.getText().toString().trim());

        String id = databasePositions.push().getKey();
        Position pos = new Position(id, longitude, latitude);

        databasePositions.child(id).setValue(pos);
        Toast.makeText(this, "Position added", Toast.LENGTH_LONG).show();
    }

}
