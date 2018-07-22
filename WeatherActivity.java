package com.example.kowshick.travelmate;

import android.Manifest;
import android.app.SearchManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class WeatherActivity extends AppCompatActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;
    public static double latitude=23.750854;
    public static double longitude=90.393527;
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        //Location
        client= LocationServices.getFusedLocationProviderClient(this);
        getDeviceCurrentLocation();
        request=new LocationRequest();
        callback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location: locationResult.getLocations()){
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    //getNearByData();
                }
            }
        };
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);
        getDeviceUpdatedLocation();


    }


    public void changefragments(View view) {
        Fragment fragment=null;
        switch (view.getId()){
            case R.id.current:
                fragment=new CurrentWeatherFragments();
                break;
            case R.id.forcast:
               fragment=new ForcastWeatherFragments();
                break;
        }
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        ft.replace(R.id.fragmentContainer,fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    //Location
    public boolean checkLoctionPermission(){
        if(ActivityCompat.checkSelfPermission(WeatherActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(WeatherActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},111);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==111&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
            getDeviceCurrentLocation();

        }
    }

    private void getDeviceUpdatedLocation() {
        if(checkLoctionPermission()) {
            client.requestLocationUpdates(request, callback, null);
            //getWeatherData(latitude,longitude);
        }
        else
            checkLoctionPermission();
    }

    public void getDeviceCurrentLocation(){

        if(checkLoctionPermission()) {
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        return;
                    }
                    else {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        CurrentWeatherFragments current=new CurrentWeatherFragments();
                        ft.add(R.id.fragmentContainer,current);
                        ft.addToBackStack(null);
                        ft.commit();
                        //getNearByData();
                        //Toast.makeText(getActivity(), "Lat:"+latitude+"  "+"lon:"+longitude, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        else {
            checkLoctionPermission();
        }
    }
}
