package com.example.diti.redminemobileclient.activities;

import android.accounts.AccountAuthenticatorActivity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.fragments.LoginViaApiKeyFragment;
import com.example.diti.redminemobileclient.fragments.LoginViaLoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity
        implements LoginViaLoginFragment.OnLoginViaLoginFragmentInteractionListener,
                   LoginViaApiKeyFragment.OnLoginViaApiKeyFragmentInteractionListener {

    private static final String TAG              = "LoginActivity";
    public static final  String EXTRA_TOKEN_TYPE = "com.example.diti.redminemobileclient.EXTRA_TOKEN_TYPE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginViaLoginFragment fragment = LoginViaLoginFragment.newInstance();
        FragmentManager       fm       = getFragmentManager();
        fm.beginTransaction().add(R.id.login_fragment_container, fragment).commit();
    }


    @Override
    public void onLoginSuccess(Bundle result) {
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void openLoginViaApiKeyFragment() {
        LoginViaApiKeyFragment fragment = LoginViaApiKeyFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, fragment)
                            .commit();
    }
}

