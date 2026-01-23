package com.findbackgroundlocationvd;

import android.app.*;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.location.*;

public class LocationService extends Service {

    private static final String CHANNEL_ID = "find_bg_location_vd";
    private FusedLocationProviderClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        client = LocationServices.getFusedLocationProviderClient(this);
        createChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Background Location")
                .setContentText("Location tracking is active")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setOngoing(true)
                .build();

        startForeground(111, notification);
        startUpdates();

        return START_STICKY;
    }

    private void startUpdates() {
        LocationRequest request = LocationRequest.create()
                .setInterval(20000)
                .setFastestInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        client.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
        );
    }

   private final LocationCallback callback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult result) {
        Location loc = result.getLastLocation();
        if (loc != null) {

            // ✅ ADD THIS LOG
            Log.d(
                "LocationService",
                "lat=" + loc.getLatitude() + ", lng=" + loc.getLongitude()
            );

            sendToJS(loc);
        }
    }
};


    private void sendToJS(Location loc) {
        if (LocationModule.reactContext == null) return;

        WritableMap map = Arguments.createMap();
        map.putDouble("latitude", loc.getLatitude());
        map.putDouble("longitude", loc.getLongitude());

        LocationModule.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onLocation", map);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Find Background Location VD",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
