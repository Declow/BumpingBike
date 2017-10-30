package h.group.sem.bumpingbike;

/**
 * Created by Morten on 30-10-2017.
 */

public class Position {

    double longitude, latitude;
    String id;

    public Position(){

    }

    public Position(String id, double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
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
