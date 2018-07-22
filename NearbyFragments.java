package com.example.kowshick.travelmate;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragments extends Fragment {
    private Spinner placeSp,disSp;
    private ListView li;
    private Button btn;
    private String area;
    private String dis;
    private TextView noPl;
    public static double longitude=90.393527;
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;
    private double latitude=23.750854;
   // private double longitude=90.393527;
    private WeatherApi weatherApi;
    private static final String CURRENT_BASE_URL="https://maps.googleapis.com/maps/api/place/nearbysearch/";

    public NearbyFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_nearby_fragments, container, false);
        placeSp=view.findViewById(R.id.placeSp);
        disSp=view.findViewById(R.id.disSp);
        btn=view.findViewById(R.id.find);
        li=view.findViewById(R.id.nearbyList);
        noPl=view.findViewById(R.id.noPlace);
        client= LocationServices.getFusedLocationProviderClient(getActivity());

        Retrofit retrofit=new Retrofit.Builder().baseUrl(CURRENT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        weatherApi= retrofit.create(WeatherApi.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,getAllAreas());
        placeSp.setAdapter(adapter);
        placeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                area = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> disadapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,getAllDis());
        disSp.setAdapter(disadapter);
        disSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dis = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //getDeviceCurrentLocation();
        request=new LocationRequest();
        callback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location: locationResult.getLocations()){
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    getNearByData();
                }
            }
        };
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);
        //getDeviceUpdatedLocation();


       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //getNearByData();
               getDeviceCurrentLocation();


           }
       });
        return view;

    }
    //Location
    public boolean checkLoctionPermission(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},111);
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
                        getNearByData();
                        Toast.makeText(getActivity(), "Lat:"+latitude+"  "+"lon:"+longitude, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        else {
            checkLoctionPermission();
        }
    }


    public void getNearByData() {
        String apiKey = getString(R.string.nearby_api_key);
        String customUrl = String.format("json?location="+latitude+","+longitude+"&radius="+dis+"&type="+area+"&key="+apiKey);
        Call<NearbyResponse> nearbyResponseCall = weatherApi.getNearby(customUrl);
        nearbyResponseCall.enqueue(new Callback<NearbyResponse>() {
            @Override
            public void onResponse(Call<NearbyResponse> call, Response<NearbyResponse> response) {
                if (response.code() == 200) {
                   NearbyResponse nearbyResponse = response.body();
                    List<NearbyResponse.Result> lists = new ArrayList<>();
                    lists = nearbyResponse.getResults();
                    if(lists.size()>0) {
                        NearbyAdapter adapter = new NearbyAdapter(getActivity(), lists);
                        li.setAdapter(adapter);
                    }
                    else{
                        noPl.setVisibility(View.VISIBLE);
                    }
                   Toast.makeText(getActivity(), area.toLowerCase(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<NearbyResponse> call, Throwable t) {
                Log.e("nearby", "onFailure: " + t.getMessage());
            }
        });
    }


    private final List<String> getAllAreas(){
        List<String>areas = new ArrayList<>();
        areas.add("restaurant");
        areas.add("bank");
        areas.add("atm");
        areas.add("hospital");
        areas.add("sopping_mall");
        areas.add("mosque");
        areas.add("bus_station");
        areas.add("police_station");
        Collections.sort(areas);
        return areas;
    }

    private final List<String> getAllDis(){
        List<String>diss = new ArrayList<>();
        diss.add("500");
        diss.add("1000");
        diss.add("1500");
        diss.add("2000");
        diss.add("2500");
        diss.add("3000");
        diss.add("3500");
        diss.add("4000");
        diss.add("4500");
        diss.add("5000");
        return diss;
    }

}
