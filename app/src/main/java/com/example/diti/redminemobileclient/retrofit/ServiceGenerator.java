package com.example.diti.redminemobileclient.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.diti.redminemobileclient.R;

import java.io.File;

import okhttp3.Cache;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    //TODO: move to settings, no hardcoding
    //private static String API_BASE_URL = "http://redmine.igs.local/";
    //private static String API_BASE_URL = "https://redmine.i-gs.ru/";
    private static OkHttpClient.Builder httpClient   = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit;

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY);

    public static <S> S createService(Class<S> serviceClass, String login, String password,
                                      Context context) throws NullPointerException {
        File  cachePath = context.getCacheDir();
        int   cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache     = new Cache(cachePath, cacheSize);

        httpClient.cache(cache);

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(login, password);
            AuthenticationInterceptor authinterceptor = new AuthenticationInterceptor(authToken,
                                                                                      true);
            buildService(authinterceptor, context);
        }
        if (retrofit == null) {
            throw new NullPointerException();
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken, Context
            context) throws NullPointerException {
        File  cachePath = context.getCacheDir();
        int   cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache     = new Cache(cachePath, cacheSize);

        httpClient.cache(cache);

        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor authinterceptor = new AuthenticationInterceptor(authToken,
                                                                                      false);
            buildService(authinterceptor, context);
        }
        if (retrofit == null) {
            throw new NullPointerException();
        }
        return retrofit.create(serviceClass);
    }

    private static void buildService(AuthenticationInterceptor authinterceptor, Context context) {
        String apiBaseUrl = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(
                R.string.preference_file_key), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(context.getString(R.string.settings_base_url))) {
            apiBaseUrl = sharedPreferences.getString(context.getString(R.string
                                                                               .settings_base_url),
                                                     "");
        }
        CacheInterceptor cacheInterceptor = new CacheInterceptor();

        if (!httpClient.interceptors().contains(logging) || !httpClient.interceptors()
                                                                       .contains(authinterceptor)) {
            if (!httpClient.interceptors().contains(authinterceptor)) {
                httpClient.addInterceptor(authinterceptor);
            }
            if (!httpClient.interceptors().contains(logging)) {
                httpClient.addInterceptor(logging);
            }

            if (!httpClient.networkInterceptors().contains(cacheInterceptor)) {
                httpClient.addNetworkInterceptor(cacheInterceptor);
            }
            if (!apiBaseUrl.equals("")) {
                builder.baseUrl(apiBaseUrl);
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
    }
}
