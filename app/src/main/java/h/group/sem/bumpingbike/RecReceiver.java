package h.group.sem.bumpingbike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RecReceiver extends BroadcastReceiver {

    IRec rec;

    public RecReceiver(IRec i) {
        super();
        this.rec = i;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        rec.InsertActivity(intent);
    }
}
