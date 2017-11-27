package h.group.sem.bumpingbike.Models;

import java.util.Date;

/**
 * Created by ditlev on 11/27/17.
 * Class created to upload acceleometer data
 */

public class AccelData {

    private float x,y,z;
    private Date date;

    public AccelData(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        date = new Date();
    }

    @Override
    public String toString() {
        return "X: " + x + " y: " + y + " z:" + z;
    }
}
