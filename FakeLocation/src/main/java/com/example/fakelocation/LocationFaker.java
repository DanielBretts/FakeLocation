//package com.example.fakelocation;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationManager;
//import android.location.provider.ProviderProperties;
//import android.os.Build;
//import android.os.SystemClock;
//import android.util.Log;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//public class LocationFaker {
//
//    private final Context context;
//    private final LocationManager locationManager;
//    private final String providerName;
//
//
//    public LocationFaker(Context context, Activity activity) {
//        this.context = context;
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        providerName = LocationManager.GPS_PROVIDER;
//    }
//
//    public void startFakingLocation(double latitude, double longitude, float accuracy) {
//        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            throw new SecurityException("Permission ACCESS_FINE_LOCATION is required");
//        }
//
//        // Ensure your app is set as the mock location app in developer settings
//        locationManager.addTestProvider(providerName, false, false, false, false, true, true, true, ProviderProperties.POWER_USAGE_LOW, ProviderProperties.ACCURACY_FINE);
//        locationManager.setTestProviderEnabled(providerName, true);
//
//        Location newLocation = new Location(providerName);
//        newLocation.setLatitude(latitude);
//        newLocation.setLongitude(longitude);
//        newLocation.setAccuracy(accuracy);
//        newLocation.setTime(System.currentTimeMillis());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
//        }
//
//        locationManager.setTestProviderLocation(providerName, newLocation);
//
//    }
//
//    public void stopFakingLocation() {
//        if (locationManager.getProvider(providerName) != null) {
//            locationManager.removeTestProvider(providerName);
//        }
//    }
//}

package com.example.fakelocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocationFaker {

    private final Context context;
    private final LocationManager locationManager;
    private final String providerName;
    private boolean isProviderAdded = false;

    private ScheduledExecutorService scheduler;

    public LocationFaker(Context context, Activity activity) {
        this.context = context;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        providerName = LocationManager.GPS_PROVIDER;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public synchronized void startFakeLocation(double latitude, double longitude, float accuracy) {
        stopFakeLocation();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable fakeLocationTask = new Runnable() {
            @Override
            public void run() {
                startFakingLocation(latitude, longitude, accuracy);
            }
        };

        scheduler.scheduleAtFixedRate(fakeLocationTask, 0, 3, TimeUnit.SECONDS);
    }

    public synchronized void stopFakeLocation() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void startFakingLocation(double latitude, double longitude, float accuracy) {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Permission ACCESS_FINE_LOCATION is required");
        }

        // Ensure your app is set as the mock location app in developer settings
        if (!isProviderAdded) {
            try {
                locationManager.addTestProvider(providerName, false, false, false, false, true, true, true, ProviderProperties.POWER_USAGE_LOW, ProviderProperties.ACCURACY_FINE);
            } catch (IllegalArgumentException e) {
                Log.w("LocationFaker", "Provider already exists");
            }
            locationManager.setTestProviderEnabled(providerName, true);
            isProviderAdded = true;
        }

        Location newLocation = new Location(providerName);
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        newLocation.setAccuracy(accuracy);
        newLocation.setTime(System.currentTimeMillis());
        newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        locationManager.setTestProviderLocation(providerName, newLocation);
    }


}
