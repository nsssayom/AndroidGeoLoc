package com.softopian.geolocation;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    public Button button, button2;
    public TextView textview, textview2;
    public EditText etlon, etlat;

    public FusedLocationProviderClient mFusedLocationClient;

    public double lon, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        textview = findViewById(R.id.textView);
        textview2 = findViewById(R.id.textView2);

        etlon = findViewById(R.id.tlon);
        etlat = findViewById(R.id.tlat);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findGeoCode();
            }
        });


        Intent intent = new Intent(getBaseContext(), AlarmActivity.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),
                1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 10);
        long timeMillis = c.getTimeInMillis();


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
        }
    }

    void findGeoCode() {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addresses = null;
        try {

            double la, lo;
            la = Double.parseDouble(etlat.getText().toString());
            lo = Double.parseDouble(etlon.getText().toString());

            Log.d("DOUBLE", la + " : " + lat + ", " + lo + " : " + lon);

            addresses = geocoder.getFromLocation(la, lo, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            // Thoroughfare seems to be the street name without numbers
            String street = "";

            street += "Admin: " + address.getAdminArea() + "\n";
            street += "Country: " + address.getCountryName() + "\n";
            street += "Feature: " + address.getFeatureName() + "\n";
            street += "Locality: " + address.getLocality() + "\n";
            street += "Postal: " + address.getPostalCode() + "\n";
            street += "Premises: " + address.getPremises() + "\n";
            street += "SubAdmin: " + address.getSubAdminArea() + "\n";
            street += "SubLocality: " + address.getSubLocality() + "\n";
            street += "Throughfare: " + address.getThoroughfare() + "\n";
            street += "SubThroughfare: " + address.getSubThoroughfare() + "\n";
            street += "Extras: " + address.getExtras() + "\n";
            street += "AddressLine: " + address.getAddressLine(0) + "\n";
            street += "MaxAddressLine: " + address.getMaxAddressLineIndex() + "\n";

            textview2.setText(street);
        }
    }

    void getLocation() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                            etlon.setText(String.valueOf(lon));
                            etlat.setText(String.valueOf(lat));

                            textview.setText(lat + ", " + lon);
                        }
                    }
                });
    }
}
