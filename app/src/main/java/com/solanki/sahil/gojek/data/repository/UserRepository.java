package com.solanki.sahil.gojek.data.repository;

import android.location.LocationProvider;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.solanki.sahil.gojek.data.network.Api;
import com.solanki.sahil.gojek.data.provider.App;
import com.solanki.sahil.gojek.data.provider.CustomSharedPreference;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private Api api;
    private final static String API_KEY = "77c941519101d96b593ddccf84f1b017";


    public UserRepository(Api api) {
        this.api = api;
    }


    public LiveData<String> result() {

        final MutableLiveData<String> resultResponse = new MutableLiveData<>();
        String location = CustomSharedPreference.getLocation(App.getContext());
        Log.e("jaejbfafb(#**$", "result: "+location);
        Call<ResponseBody> call = api.getCurrentWeather(API_KEY, location);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        resultResponse.setValue(response.body().string());
                    } catch (IOException e) {
                        resultResponse.setValue(e.getMessage());
                    }
                } else {
                    try {
                        resultResponse.setValue(response.errorBody().string());
                    } catch (IOException e) {
                        resultResponse.setValue(e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resultResponse.setValue(t.getMessage());
            }
        });

        return resultResponse;
    }
}
