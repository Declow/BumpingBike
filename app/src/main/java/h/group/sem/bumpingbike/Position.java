package h.group.sem.bumpingbike;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Morten on 30-10-2017.
 */

public class Position {

    double longitude, latitude;
    String id;
    String timeStamp;
    ArrayList<Position> PositionsInRange;


    public Position(){
        this.timeStamp = new Date().toString();
    }

    public Position(String id, double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.timeStamp = new Date().toString();
        this.PositionsInRange = new ArrayList<Position>();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getId() {
        return id;
    }

    public int getCountPositionsInRange() { return PositionsInRange.size(); }

    public void PositionsFoundInRange(Position newPosition) {
        this.PositionsInRange.add(newPosition);
    }


}
