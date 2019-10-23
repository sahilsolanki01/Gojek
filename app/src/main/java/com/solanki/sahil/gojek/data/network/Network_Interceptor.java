package com.solanki.sahil.gojek.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Response;

public class Network_Interceptor implements Interceptor {
    private Context context;

    public Network_Interceptor(Context context) {
        this.context = context.getApplicationContext();
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        if(!isConnected())
            throw new IOException("Check network connectivity and try again!");

        return chain.proceed(chain.request());

    }

    private Boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
