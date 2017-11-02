package h.group.sem.bumpingbike;

import java.util.Date;

/**
 * Created by Morten on 30-10-2017.
 */

public class Position {

    double longitude, latitude;
    String id;
    String timeStamp;

    public Position(){
        this.timeStamp = new Date().toString();
    }

    public Position(String id, double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.timeStamp = new Date().toString();
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
}
