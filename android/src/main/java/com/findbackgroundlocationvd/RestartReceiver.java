package com.findbackgroundlocationvd;

import android.content.*;
import android.os.Build;
import android.util.Log;

public class RestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LocationService", "RestartReceiver triggered");

        Intent serviceIntent = new Intent(context, LocationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
