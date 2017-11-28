package h.group.sem.bumpingbike.Utils;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DataReceiver extends BroadcastReceiver {

    private DataCollectionService d;
    public DataReceiver (DataCollectionService d) {
        this.d = d;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("StopDataCollection")) {
            d.stopService();
        }
    }
}
