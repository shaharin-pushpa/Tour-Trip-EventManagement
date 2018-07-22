package com.example.kowshick.travelmate;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForcastWeatherFragments extends Fragment {
    private static final String CURRENT_BASE_URL="http://api.openweathermap.org/data/2.5/";
    private WeatherApi weatherApi;
    private double latitude=23.750854;
    private double longitude=90.393527;
    public String unit="metric";
    private WeatherAdapter adapter;
    RecyclerView recyclerView;
    TextView temTv,dateTv,cityTv;

    public ForcastWeatherFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fragment_forcast_weather_fragments, container, false);
        recyclerView=view.findViewById(R.id.forcastRecyclerView);
        Retrofit retrofit=new Retrofit.Builder().baseUrl(CURRENT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        weatherApi= retrofit.create(WeatherApi.class);
        getWeatherData();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.navigation_drawer, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_farehite:
                unit="imperial";
                getWeatherData();
                break;
            case R.id.action_celcius:
                unit="metric";
                getWeatherData();
                break;
            case R.id.back_list:
                Intent i = new Intent(getActivity(), NavigationDrawer.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0,0);
        }

        return true;
    }

    public void getWeatherData(){
        String apiKey=getString(R.string.weather_api_key);
        String customUrl=String.format("forecast?lat=%f&lon=%f&units=%s&cnt=10&appid=%s",WeatherActivity.latitude,WeatherActivity.longitude,unit,apiKey);
        Call<ForcastWeatherResponse> forcastWeatherResponseCall=weatherApi.getForcastWeather(customUrl);
        forcastWeatherResponseCall.enqueue(new Callback<ForcastWeatherResponse>() {
            @Override
            public void onResponse(Call<ForcastWeatherResponse> call, Response<ForcastWeatherResponse> response) {
                if (response.code() == 200) {
                    ForcastWeatherResponse forcastWeatherResponse = response.body();
                    List<ForcastWeatherResponse.ListFor> lists= new ArrayList<>();
                    lists = forcastWeatherResponse.getList();
                    //Toast.makeText(getActivity(), ""+lists.size(), Toast.LENGTH_SHORT).show();
                    adapter = new WeatherAdapter(getActivity(), lists,unit);
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(adapter);

                    // Toast.makeText(getActivity(), "Forecast Value Retrived", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ForcastWeatherResponse> call, Throwable t) {
                Log.e("forecast", "onFailure: "+t.getMessage());
            }
        });
    }
}
