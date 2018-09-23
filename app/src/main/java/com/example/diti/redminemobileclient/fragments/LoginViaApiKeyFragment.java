package com.example.diti.redminemobileclient.fragments;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.authenticator.RedmineAccount;
import com.example.diti.redminemobileclient.model.User;
import com.example.diti.redminemobileclient.model.Users;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViaApiKeyFragment extends Fragment {
    private static final String TAG = "LoginViaApiKeyFragment";
    private EditText        mApiKey;
    private Button          mSignInButton;
    private Button          mLoginViaLoginButton;
    private TextInputLayout mApiKeyTextInputLayout;
    private View            mLoginFormView;
    private ProgressBar     mProgressView;


    private OnLoginViaApiKeyFragmentInteractionListener mListener;

    public LoginViaApiKeyFragment() {
    }


    public static LoginViaApiKeyFragment newInstance() {
        LoginViaApiKeyFragment fragment = new LoginViaApiKeyFragment();
        Bundle                 args     = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_via_api_key, container, false);

        mApiKey = (EditText) v.findViewById(R.id.apikey);
        mApiKey.setText("90ae675e9cc9ce2a91d0ab889f3b46fa82f4ca28"); //TODO: delete, just for debug
        mApiKeyTextInputLayout = (TextInputLayout) v.findViewById(R.id.apikeyWrapper);
        mSignInButton = (Button) v.findViewById(R.id.apikey_sign_in_button);
        mLoginFormView = (View) v.findViewById(R.id.apikey_login_form);
        mProgressView = (ProgressBar) v.findViewById(R.id.login_progress);
        mLoginViaLoginButton = (Button) v.findViewById(R.id.login_via_login_button);

        mApiKeyTextInputLayout.setHint(getString(R.string.prompt_api_key));

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginViaLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.openLoginViaLoginFragment();
            }
        });
        return v;
    }

    public void attemptLogin() {
        String baseUrl = mListener.getBaseUrl();
        //TODO: add base url into sharedpref
        String apiKey = mApiKey.getText().toString();
        if (TextUtils.isEmpty(apiKey)) {
            mApiKey.setError(getString(R.string.error_empty_APIKEY));
            mApiKey.requestFocus();
        }else{
            showProgress(true);

            //adding url into sharedpreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences
                    (getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.settings_base_url), baseUrl);
            editor.commit();

            RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient
                    (apiKey,
                     getActivity());
            Call<Users> call =
                    client.reposForUser();
            call.enqueue(new Callback<Users>() {
                @Override
                public void onResponse(@NonNull Call<Users> call,
                                       @NonNull Response<Users> response) {
                    showProgress(false);
                    if (response.isSuccessful()) {
                        User user = response.body().getUser();
                        String         login   = user.getLogin();
                        String email = user.getMail();
                        String name = user.getLastname() + " " + user.getFirstname();
                        RedmineAccount account = new RedmineAccount(login);
                        Bundle         result  = new Bundle();
                        AccountManager am = AccountManager.get(getActivity()
                                                                       .getApplicationContext());
                        Bundle userOptions = new Bundle();
                        userOptions.putString(getString(R.string.AM_BASE_URL), baseUrl);
                        userOptions.putString(getString(R.string.AM_EMAIL), email);
                        userOptions.putString(getString(R.string.AM_FIO), name);
                        if (am.addAccountExplicitly(account, apiKey, userOptions)) {
                            result.putString(AccountManager.KEY_ACCOUNT_NAME, login);
                            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                            result.putString(AccountManager.KEY_PASSWORD, apiKey);
                            result.putString(getString(R.string.AM_EMAIL), email);
                            result.putString(getString(R.string.AM_FIO), name);
                            am.setAuthToken(account, RedmineAccount.TOKEN_FULL_ACCESS, apiKey);
                        } else {
                            result.putString(AccountManager.KEY_ERROR_MESSAGE,
                                             getString(R.string.account_already_exists));
                        }

                        mListener.onLoginSuccess(result);
                    } else {
                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG)
                             .show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                    Log.d(TAG, t.getLocalizedMessage());
                    showProgress(false);
                    mListener.onLoginFailedWrongHostName();
                }
            });
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginViaApiKeyFragmentInteractionListener) {
            mListener = (OnLoginViaApiKeyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                               + " must implement OnLoginViaApiKeyFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnLoginViaApiKeyFragmentInteractionListener {
        void onLoginSuccess(Bundle result);

        void openLoginViaLoginFragment();

        String getBaseUrl();

        void onLoginFailedWrongHostName();
    }
}
