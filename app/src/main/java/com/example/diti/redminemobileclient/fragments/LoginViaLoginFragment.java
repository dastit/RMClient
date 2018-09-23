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
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.authenticator.RedmineAccount;
import com.example.diti.redminemobileclient.model.User;
import com.example.diti.redminemobileclient.model.Users;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViaLoginFragment extends Fragment {
    private static final String TAG = "LoginViaLoginFragment";

    // UI references.
    private EditText        mLoginView;
    private EditText        mPasswordView;
    private View            mProgressView;
    private Button          mSignInButton;
    private Button          mSignViaApiKey;
    private View            mLoginFormView;
    private TextInputLayout mLoginTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;

    String login;
    String password;

    private OnLoginViaLoginFragmentInteractionListener mListener;

    public LoginViaLoginFragment() {
    }

    public static LoginViaLoginFragment newInstance() {
        LoginViaLoginFragment fragment = new LoginViaLoginFragment();
        Bundle                args     = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_via_login, container, false);

        mLoginFormView = v.findViewById(R.id.login_form);
        mProgressView = v.findViewById(R.id.login_progress);
        mLoginTextInputLayout = (TextInputLayout) v.findViewById(R.id.loginWrapper);
        mPasswordTextInputLayout = (TextInputLayout) v.findViewById(R.id.passwordWrapper);
        mLoginTextInputLayout.setHint(getString(R.string.prompt_login));
        mPasswordTextInputLayout.setHint(getString(R.string.prompt_password));

        mLoginView = (EditText) v.findViewById(R.id.login);
        mPasswordView = (EditText) v.findViewById(R.id.password);
        mSignInButton = (Button) v.findViewById(R.id.sign_in_button);
        mSignViaApiKey = (Button) v.findViewById(R.id.login_via_apikey_button);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(),
                                                InputMethodManager.HIDE_IMPLICIT_ONLY);
                    mSignInButton.performClick();
                    return true;
                }
                return false;
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = getActivity().getCurrentFocus();
                if (view != null) {
                    ((InputMethodManager) getActivity().getSystemService(Context
                                                                                 .INPUT_METHOD_SERVICE))
                            .
                                    hideSoftInputFromWindow(view.getWindowToken(),
                                                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                attemptLoginViaLoginPassword();
            }
        });

        mSignViaApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.openLoginViaApiKeyFragment();
            }
        });
        return v;
    }

    private void attemptLoginViaLoginPassword() {
        String baseUrl = mListener.getBaseUrl();

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        login = mLoginView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel    = false;
        View    focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else if (!isEmailValid(login)) {
            mLoginView.setError(getString(R.string.error_invalid_email));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            //adding url into sharedpreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences
                    (getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.settings_base_url), baseUrl);
            editor.commit();

            try {
                RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(
                        login,
                        password,
                        getActivity());
                Call<Users> call =
                        client.reposForUser();
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        showProgress(false);
                        if (response.isSuccessful()) {
                            User           user      = response.body().getUser();
                            String         authToken = user.getApiKey();
                            String         email     = user.getMail();
                            String         name      = user.getFirstname() + user.getLastname();

                            RedmineAccount account   = new RedmineAccount(login);
                            Bundle         result    = new Bundle();
                            AccountManager am = AccountManager.get(getActivity()
                                                                           .getApplicationContext());
                            Bundle userOptions = new Bundle();
                            userOptions.putString(getString(R.string.AM_BASE_URL), baseUrl);
                            userOptions.putString(getString(R.string.AM_EMAIL), email);
                            userOptions.putString(getString(R.string.AM_FIO), name);
                            if (am.addAccountExplicitly(account, authToken, userOptions)) {
                                result.putString(AccountManager.KEY_ACCOUNT_NAME, login);
                                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                                result.putString(AccountManager.KEY_PASSWORD, authToken);
                                result.putString(getString(R.string.AM_EMAIL), email);
                                result.putString(getString(R.string.AM_FIO), name);
                                am.setAuthToken(account, RedmineAccount.TOKEN_FULL_ACCESS,
                                                authToken);
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
                    public void onFailure(Call<Users> call, Throwable t) {
                        Log.d(TAG, t.getLocalizedMessage());
                        showProgress(false);
                        mListener.onLoginFailedWrongHostName();
                    }
                });
            } catch (NullPointerException e) {
                Log.e(TAG, e.getLocalizedMessage());
                Toast.makeText(getActivity(), R.string.internal_error, Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean isEmailValid(String email) {

        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginViaLoginFragmentInteractionListener) {
            mListener = (OnLoginViaLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                               + " must implement OnLoginViaLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnLoginViaLoginFragmentInteractionListener {
        void onLoginSuccess(Bundle result);

        void openLoginViaApiKeyFragment();

        String getBaseUrl();

        void onLoginFailedWrongHostName();
    }
}
