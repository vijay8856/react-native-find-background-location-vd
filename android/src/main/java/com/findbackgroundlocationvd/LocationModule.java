package com.findbackgroundlocationvd;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LocationModule extends ReactContextBaseJavaModule {

    static ReactApplicationContext reactContext;

    LocationModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "FindBackgroundLocationVD";
    }

    @ReactMethod
    public void start() {
        Intent intent = new Intent(getReactApplicationContext(), LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getReactApplicationContext().startForegroundService(intent);
        } else {
            getReactApplicationContext().startService(intent);
        }
    }

    @ReactMethod
    public void stop() {
        Intent intent = new Intent(getReactApplicationContext(), LocationService.class);
        getReactApplicationContext().stopService(intent);
    }
}
