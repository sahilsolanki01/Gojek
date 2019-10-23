package com.solanki.sahil.gojek.data.network;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {

    private static Retrofit retrofit = null;


    public static Api getApi(Network_Interceptor network_interceptor){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                HttpUrl url = chain.request()
                        .url()
                        .newBuilder()
                        .build();

                Request request = chain.request()
                        .newBuilder()
                        .url(url)
                        .build();

                return chain.proceed(request);
            }
        };

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(requestInterceptor).addInterceptor(network_interceptor).addInterceptor(interceptor).build();

        if(retrofit == null){

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl("http://api.weatherstack.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(Api.class);
    }








}
