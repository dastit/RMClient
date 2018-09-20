package com.example.diti.redminemobileclient.activities;

import android.accounts.AccountAuthenticatorActivity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.fragments.LoginViaApiKeyFragment;
import com.example.diti.redminemobileclient.fragments.LoginViaLoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity
        implements LoginViaLoginFragment.OnLoginViaLoginFragmentInteractionListener,
                   LoginViaApiKeyFragment.OnLoginViaApiKeyFragmentInteractionListener {

    private static final String TAG = "LoginActivity";
    private TextInputLayout mBaseUrlInputLayout;
    public EditText mBaseUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBaseUrlInputLayout = (TextInputLayout)findViewById(R.id.baseUrlWrapper);
        mBaseUrlInputLayout.setHint("Host address");
        mBaseUrl = (EditText)findViewById(R.id.base_url);
        //TODO: for debug
        mBaseUrl.setText("http://redmine.igs.local/");

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
    public void openLoginViaLoginFragment() {
        LoginViaLoginFragment fragment = LoginViaLoginFragment.newInstance();
        getFragmentManager().beginTransaction()
                            .replace(R.id.login_fragment_container, fragment)
                            .commit();
    }

    @Override
    public void openLoginViaApiKeyFragment() {
        LoginViaApiKeyFragment fragment = LoginViaApiKeyFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, fragment)
                            .commit();
    }

    @Override
    public String getBaseUrl() {
        return mBaseUrl.getText().toString();
    }

    @Override
    public void onLoginFailedWrongHostName() {
        mBaseUrl.setError(getString(R.string.error_wrong_host_name));
        mBaseUrl.requestFocus();
    }
}

