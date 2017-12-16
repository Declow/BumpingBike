package h.group.sem.bumpingbike.Models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ditlev on 11/27/17.
 * Class created to upload acceleometer data
 */

public class AccelData implements Serializable {

    private float x,y,z;
    private long timeStampInMS;

    public AccelData(float x, float y, float z, long startTime) {
        this.x = x;
        this.y = y;
        this.z = z;
        timeStampInMS = (System.currentTimeMillis() - startTime);
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setTimeStampInMS(long timeStamp) { this.timeStampInMS = timeStamp; }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public long getTimeStampInMS() {
        return timeStampInMS;
    }

    @Override
    public String toString() {
        return "X: " + x + " y: " + y + " z:" + z + " time: " + timeStampInMS;
    }
}
