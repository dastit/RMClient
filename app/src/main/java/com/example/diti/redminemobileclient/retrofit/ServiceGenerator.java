package com.example.diti.redminemobileclient.retrofit;

import android.text.TextUtils;

import java.io.File;

import okhttp3.Cache;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    //TODO: move to settings, no hardcoding
    private static String API_BASE_URL = "https://redmine.i-gs.ru/";

    private static  OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    public static <S> S createService(Class<S> serviceClass, File cachePath){
        return createService(serviceClass, null, null, cachePath);
    }

    public static <S> S createService(Class<S> serviceClass, String login, String password, File cachePath){
        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)){
            String authToken = Credentials.basic(login, password);
            return  createService(serviceClass, authToken, cachePath);
        }
        return createService(serviceClass, null, cachePath);
    }
    public static <S> S createService(Class<S> serviceClass, final String authToken, File cachePath){

        int   cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache     = new Cache(cachePath, cacheSize);

        httpClient.cache(cache);

        if(!TextUtils.isEmpty(authToken)){
            AuthenticationInterceptor authinterceptor = new AuthenticationInterceptor (authToken);
            CacheInterceptor cacheInterceptor = new CacheInterceptor();

            if(!httpClient.interceptors().contains(logging) || !httpClient.interceptors().contains(authinterceptor)){
                if(!httpClient.interceptors().contains(authinterceptor)){
                    httpClient.addInterceptor(authinterceptor);
                }
                if(!httpClient.interceptors().contains(logging)){
                    httpClient.addInterceptor(logging);
                }

                if(!httpClient.networkInterceptors().contains(cacheInterceptor)){
                    httpClient.addNetworkInterceptor(cacheInterceptor);
                }

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }
}
