package com.solanki.sahil.gojek.data.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface Api {

    @GET("current")
    Call<ResponseBody> getCurrentWeather(@Query("access_key") String key, @Query("query") String location);

   // @GET("forecast")
    // Call<ResponseBody> getFutureWeather(@Query("access_key") String key, @Query("query") String location, @Query("forecast_days") int days);

}
