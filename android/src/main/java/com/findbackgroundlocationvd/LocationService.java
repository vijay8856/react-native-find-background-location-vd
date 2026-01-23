package com.findbackgroundlocationvd;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


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
    .setContentText("Tracking location in background")
    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
    .setOngoing(true)
    .setCategory(Notification.CATEGORY_SERVICE)
    .build();


        startForeground(111, notification);
        startUpdates();

        return START_STICKY;
    }
@Override
public void onTaskRemoved(Intent rootIntent) {
    Log.d("LocationService", "App removed, scheduling restart");

    Intent restartIntent = new Intent(getApplicationContext(), RestartReceiver.class);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(
            getApplicationContext(),
            1,
            restartIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );

    AlarmManager alarmManager =
            (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 5000, // 5 seconds
            pendingIntent
    );

    super.onTaskRemoved(rootIntent);
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
            "Background Location Tracking",
            NotificationManager.IMPORTANCE_HIGH // 🔥 IMPORTANT
        );
        channel.setDescription("Location tracking is active");
        channel.setShowBadge(false);

        NotificationManager manager =
            (NotificationManager) getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
