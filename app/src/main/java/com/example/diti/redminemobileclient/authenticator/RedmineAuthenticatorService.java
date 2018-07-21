package com.example.diti.redminemobileclient.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.diti.redminemobileclient.authenticator.RedmineAuthenticator;

public class RedmineAuthenticatorService extends Service {
    RedmineAuthenticator authenticator;
    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new RedmineAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
