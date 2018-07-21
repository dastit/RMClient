package com.example.diti.redminemobileclient.retrofit;

import android.text.TextUtils;

import com.example.diti.redminemobileclient.authenticator.AuthenticationInterceptor;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static String API_BASE_URL = "https://redmine.i-gs.ru/";

    private static  OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    public static <S> S createService(Class<S> serviceClass){
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String login, String password){
        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)){
            String authToken = Credentials.basic(login, password);
            return  createService(serviceClass, authToken);
        }
        return createService(serviceClass, null);
    }
    public static <S> S createService(Class<S> serviceClass, final String authToken){

        if(!TextUtils.isEmpty(authToken)){
            AuthenticationInterceptor authinterceptor = new AuthenticationInterceptor (authToken);

            if(!httpClient.interceptors().contains(logging) || !httpClient.interceptors().contains(authinterceptor)){
                if(!httpClient.interceptors().contains(authinterceptor)){
                    httpClient.addInterceptor(authinterceptor);
                }
                if(!httpClient.interceptors().contains(logging)){
                    httpClient.addInterceptor(logging);
                }

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }
}
