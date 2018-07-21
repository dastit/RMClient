package com.example.diti.redminemobileclient.authenticator;

import android.accounts.Account;
import android.os.Parcel;

public class RedmineAccount  extends Account {

    public static final String TYPE = "com.example.diti.redminemobileclient";

    public static final String TOKEN_FULL_ACCESS = "com.example.diti.redminemobileclient.TOKEN_FULL_ACCESS";

    public static final String KEY_PASSWORD = "com.example.diti.redminemobileclient.KEY_PASSWORD";

    public RedmineAccount(Parcel in) {
        super(in);
    }

    public RedmineAccount(String name) {
        super(name, TYPE);
    }

}
