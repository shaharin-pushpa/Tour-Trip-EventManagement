package com.example.kowshick.travelmate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherApi {
    @GET()
    Call<CurrentWeatherResponse> getCurrentWeather(@Url String urlString);
   @GET()
    Call<ForcastWeatherResponse> getForcastWeather(@Url String urlString);
    @GET()
    Call<CurrentWeatherCity> getCurrentWeatherCity(@Url String urlString);
    @GET()
    Call<NearbyResponse> getNearby(@Url String urlString);
}
