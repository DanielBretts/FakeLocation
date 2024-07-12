package com.example.fakelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fakelocation.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LocationFaker locationFaker = new LocationFaker(getApplicationContext(), MainActivity.this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    binding.textFakeLocation.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
                    updateMapLocation(new LatLng(location.getLatitude(), location.getLongitude()));                }
            }
        };

        binding.btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fakeLat = binding.editLatitude.getText().toString();
                String fakeLng = binding.editLongitude.getText().toString();
                if (!fakeLat.isEmpty() && !fakeLng.isEmpty()) {
                    double latitude = Double.parseDouble(fakeLat);
                    double longitude = Double.parseDouble(fakeLng);
                    locationFaker.startFakeLocation(latitude, longitude, 5.0f);
                    binding.btnSetLocation.setEnabled(false);
                    binding.btnStopLocation.setEnabled(true);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter valid latitude and longitude", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnStopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationFaker.stopFakeLocation();
                binding.btnStopLocation.setEnabled(false);
                binding.btnSetLocation.setEnabled(true);
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    }

    private void updateMapLocation(LatLng latLng) {
        if (gMap != null) {
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000); // 10 seconds
        locationRequest.setFastestInterval(1000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

}
