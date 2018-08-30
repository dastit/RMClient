package com.example.diti.redminemobileclient.retrofit;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private String  authToken;
    private boolean basicFlag;

    public AuthenticationInterceptor(String token, boolean isBasic) {
        this.authToken = token;
        basicFlag = isBasic;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request         original = chain.request();
        Request.Builder builder;
        if (basicFlag) {
            builder = original.newBuilder().header("Authorization", authToken);
        } else {
            builder = original.newBuilder().header("X-Redmine-API-Key", authToken);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}
