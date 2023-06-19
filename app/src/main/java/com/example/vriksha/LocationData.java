package com.example.vriksha;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import androidx.core.app.ActivityCompat;

public class LocationData {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;

    public LocationData(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = createLocationListener();
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called when the location has changed

                // Get the latitude and longitude from the updated location
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Called when the status of the location provider has changed
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Called when the location provider is enabled
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Called when the location provider is disabled
            }
        };
    }

    public void requestLocationUpdates() {
        try {
            // Check if the app has permission to access the device's location
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            } else {
                // Permission not granted, handle the case
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void removeLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
