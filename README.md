Location Faker Library
Overview

The LocationFaker library is an Android utility designed to fake the device's location. This can be useful for testing location-based features without physically moving the device. The library requires your app to have the necessary permissions and to be set as the mock location app in the developer settings.
Features

    Set up fake GPS coordinates
    Regularly update the fake location to simulate movement
    Start and stop faking the location

Requirements

    Android device or emulator
    API level 31 (Android 12) or higher
    Permission to access fine location (ACCESS_FINE_LOCATION)
    App set as the mock location app in developer settings

Installation

    Add the library to your project.

    Request the necessary permissions in your AndroidManifest.xml:

    ```xml
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ```
Usage
Initialization

To use the LocationFaker library, you need to initialize it with the context and activity:

```java
import com.example.fakelocation.LocationFaker;

// Inside your activity or fragment
LocationFaker locationFaker = new LocationFaker(this, this);
```
Start Faking Location

To start faking the location, call the startFakeLocation method with the desired latitude, longitude, and accuracy. This method sets up a scheduler that updates the fake location every 3 seconds.

```java

double latitude = 37.7749;  // Example latitude
double longitude = -122.4194;  // Example longitude
float accuracy = 5.0f;  // Example accuracy in meters

locationFaker.startFakeLocation(latitude, longitude, accuracy);
```
Stop Faking Location

To stop faking the location, call the stopFakeLocation method. This will stop the scheduler and cease updating the fake location.

```java
locationFaker.stopFakeLocation();
```

Permissions

Ensure that you have requested the ACCESS_FINE_LOCATION permission in your activity:

```java
if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
}
```

Developer Settings

To use this library, your app must be set as the mock location app in the developer settings:

  - Enable Developer Options on your device.
  - Navigate to Settings > Developer options.
  - Find and set your app as the mock location app.

Example Code

Below is a complete example of how to use the LocationFaker in an activity:

```java

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LocationFaker locationFaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationFaker = new LocationFaker(this, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onStart() {
        super.onStart();
        
        // Start faking location
        double latitude = 37.7749;
        double longitude = -122.4194;
        float accuracy = 5.0f;
        locationFaker.startFakeLocation(latitude, longitude, accuracy);
    }

    @Override
    protected void onStop() {
        super.onStop();
        
        // Stop faking location
        locationFaker.stopFakeLocation();
    }
}
```
Notes

    This library is intended for testing and development purposes only. Misusing location spoofing can violate the terms of service of various applications and services.
    Ensure your app is set as the mock location app in developer settings before using this library.
    
Video Example

https://github.com/user-attachments/assets/e4bf4456-9caa-4460-a48a-7b7e04b4a3c7

