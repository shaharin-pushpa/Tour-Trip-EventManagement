package com.example.kowshick.travelmate;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.SEARCH_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragments extends Fragment {
    private static final String CURRENT_BASE_URL="http://api.openweathermap.org/data/2.5/";
    public static final String IMAGE_URL = "https://openweathermap.org/img/w/";
    private WeatherApi weatherApi;
    private String unit="metric";
    private double latitude=23.750854;
    private double longitude=90.393527;
    /*private WeatherAdapter adapter;
    RecyclerView recyclerView;*/
    TextView temTv,dateTv,cityTv,minTv,maxTv,sunsetTv,sunriseTv,huminityTv,pressureTv,sunnyTv;
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;
    private ImageView imgSunny,imgMax,imgSun,imgHu;
    private String city=null;

    public CurrentWeatherFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fragment_current_weather_fragments, container, false);
        temTv=view.findViewById(R.id.textViewTemp);
        dateTv=view.findViewById(R.id.textViewDate);
        cityTv=view.findViewById(R.id.textViewCity);
        minTv=view.findViewById(R.id.textViewMinValue);
        maxTv=view.findViewById(R.id.textViewMaxValue);
        sunsetTv=view.findViewById(R.id.textViewSunset);
        sunriseTv=view.findViewById(R.id.textViewSunrise);
        huminityTv=view.findViewById(R.id.textViewHumidity);
        pressureTv=view.findViewById(R.id.textViewPressure);
        sunnyTv=view.findViewById(R.id.textViewWeatherMain);
        imgSunny=view.findViewById(R.id.imageViewIcon);
        imgSun=view.findViewById(R.id.imgSun);
        imgMax=view.findViewById(R.id.imgMax);
        imgHu=view.findViewById(R.id.imgHu);
        Retrofit retrofit=new Retrofit.Builder().baseUrl(CURRENT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        weatherApi= retrofit.create(WeatherApi.class);
        getWeatherData();
        return view;
    }
    //Menu

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.navigation_drawer, menu);
        SearchManager manager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search");
        MenuItem celsius= menu.findItem(R.id.action_celcius);
        MenuItem faren = menu.findItem(R.id.action_farehite);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
               searchApi(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        //return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.back_list:
                Intent i = new Intent(getActivity(), NavigationDrawer.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0,0);

                break;
            case R.id.action_celcius:
                unit="metric";
                    getWeatherData();
                break;
            case R.id.action_farehite:
                unit="imperial";
                    getWeatherData();
                break;
            case R.id.search:
                break;
        }

        return true;
    }

    public void getWeatherData(){

        String apiKey=getString(R.string.weather_api_key);
        String customUrl=String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",WeatherActivity.latitude,WeatherActivity.longitude,unit,apiKey);
        Call<CurrentWeatherResponse> currentWeatherResponseCall=weatherApi.getCurrentWeather(customUrl);
        currentWeatherResponseCall.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                try {
                    if (response.code() == 200) {
                        CurrentWeatherResponse currentWeatherResponse = response.body();

                        if(unit.equals("metric")){
                            temTv.setText(String.valueOf(currentWeatherResponse.getMain().getTemp())+"° C");
                            minTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMin())+"° C");
                            maxTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMax())+"° C");
                        }
                        else{
                            temTv.setText(String.valueOf(currentWeatherResponse.getMain().getTemp())+"° F");
                            minTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMin())+"° F");
                            maxTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMax())+"° F");
                        }
                        cityTv.setText(currentWeatherResponse.getName());
                        huminityTv.setText(String.valueOf(currentWeatherResponse.getMain().getHumidity()) + "%");
                        pressureTv.setText(String.valueOf(currentWeatherResponse.getMain().getPressure()) + " hPa");
                        //date
                        long datetime = currentWeatherResponse.getDt();
                        Date date = new Date(datetime*1000);
                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                        String dateText = df2.format(date);
                        dateTv.setText(dateText);
                        sunnyTv.setText(currentWeatherResponse.getWeather().get(0).getDescription());
                        //time
                        long millisecond=currentWeatherResponse.getSys().getSunrise();
                        String dateString = DateFormat.format("HH:mm aa", new Date(millisecond*1000)).toString();
                        sunriseTv.setText(dateString);

                        long mil=currentWeatherResponse.getSys().getSunset();
                        String dateSun = DateFormat.format("HH:mm aa", new Date(mil*1000)).toString();
                        sunsetTv.setText(dateSun);

                        //Image

                        //String photo = currentWeatherResponse.getWeather().;
                        try {
                            String photo = (currentWeatherResponse.getWeather().get(0).getIcon());
                            String photoUri = IMAGE_URL + photo+".png";

                            Picasso.get().load(Uri.parse(photoUri)).into(imgSunny);
                            //Toast.makeText(getActivity(), photo, Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){

                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {

                Log.e("error",t.getMessage());
            }
        });
    }

   public void searchApi(String q){
        city=q;
        if(city.equals(null)){
            getWeatherData();
        }
        else{
            getSearchWeatherData();
        }
    }
    private void getSearchWeatherData() {
        String apiKey=getString(R.string.weather_api_key);
        String customUrl=String.format("weather?q=%s&units=%s&appid=%s",city,unit,apiKey);
        Call<CurrentWeatherCity> currentWeatherCityCall=weatherApi.getCurrentWeatherCity(customUrl);
        currentWeatherCityCall.enqueue(new Callback<CurrentWeatherCity>() {
            @Override
            public void onResponse(Call<CurrentWeatherCity> call, Response<CurrentWeatherCity> response) {
                try {
                    if (response.code() == 200) {
                        CurrentWeatherCity currentWeatherResponse = response.body();
                        if(unit.equals("metric")){
                            temTv.setText(String.valueOf(currentWeatherResponse.getMain().getTemp())+"° C");
                            minTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMin())+"° C");
                            maxTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMax())+"° C");
                        }
                        else{
                            temTv.setText(String.valueOf(currentWeatherResponse.getMain().getTemp())+"° F");
                            minTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMin())+"° F");
                            maxTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMax())+"° F");
                        }

                        //temTv.setText(String.valueOf(currentWeatherResponse.getMain().getTemp()));
                        cityTv.setText(currentWeatherResponse.getName());
                        sunnyTv.setText(currentWeatherResponse.getWeather().get(0).getDescription());
                        //minTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMin()));
                       // maxTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMax()));
                        huminityTv.setText(String.valueOf(currentWeatherResponse.getMain().getHumidity()) + "%");
                        pressureTv.setText(String.valueOf(currentWeatherResponse.getMain().getPressure()) + " hPa");
                        //date
                        long datetime = currentWeatherResponse.getDt();
                        Date date = new Date(datetime*1000);
                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                        String dateText = df2.format(date);
                        dateTv.setText(dateText);
                        //time
                        long millisecond=currentWeatherResponse.getSys().getSunrise();
                        String dateString = DateFormat.format("HH:mm aa", new Date(millisecond*1000)).toString();
                        sunriseTv.setText(dateString);

                        long mil=currentWeatherResponse.getSys().getSunset();
                        String dateSun = DateFormat.format("HH:mm aa", new Date(mil*1000)).toString();
                        sunsetTv.setText(dateSun);

                        //Image

                        //String photo = currentWeatherResponse.getWeather().;
                        try {
                            String photo = (currentWeatherResponse.getWeather().get(0).getIcon());
                            String photoUri = IMAGE_URL + photo+".png";

                            Picasso.get().load(Uri.parse(photoUri)).into(imgSunny);
                            Toast.makeText(getActivity(), photo, Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){

                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<CurrentWeatherCity> call, Throwable t) {

                Log.e("error",t.getMessage());
            }
        });
    }


}
