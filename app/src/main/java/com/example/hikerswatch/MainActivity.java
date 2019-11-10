package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import javax.security.auth.callback.PasswordCallback;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView latTextView;
    TextView longTextView;
    TextView accuracyTextView;
    TextView altitudeTextView;
    TextView addressTextView;
    Geocoder geocoder;
    List<Address> addresses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.latTextView);
        longTextView = findViewById(R.id.longTextView);
        accuracyTextView = findViewById(R.id.accuracyTextView);
        addressTextView = findViewById(R.id.addressTextView);
        altitudeTextView = findViewById(R.id.altitudeTextView);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastLocation != null) {
                updateLocation(lastLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public void updateLocation(Location location) {
        Log.i("Location", location.toString());
        String address = "";
        boolean addr = false;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.i("Address",addresses.toString());
            if(addresses!=null && addresses.size()>0){
                if(addresses.get(0).getThoroughfare() != null){
                    address += addresses.get(0).getThoroughfare() + "\n";
                    addr=true;
                }
                if(addresses.get(0).getLocality() != null){
                    address += addresses.get(0).getLocality() + "\n";
                    addr=true;
                }
                if(addresses.get(0).getPostalCode() != null){
                    address += addresses.get(0).getPostalCode() + "\n";
                    addr=true;
                }
                if(addresses.get(0).getAdminArea() != null){
                    address += addresses.get(0).getAdminArea() + "\n";
                    addr=true;
                }
                if(!addr){
                    address += addresses.get(0).getAddressLine(0);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        latTextView.setText("Latitude: "+String.valueOf(location.getLatitude()));
        longTextView.setText("Longitude: "+String.valueOf(location.getLongitude()));
        accuracyTextView.setText("Accuracy: "+String.valueOf(location.getAccuracy()));
        altitudeTextView.setText("Altitude: "+String.valueOf(location.getAltitude()));
        //addressTextView.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
        addressTextView.setText(address);
    }
}