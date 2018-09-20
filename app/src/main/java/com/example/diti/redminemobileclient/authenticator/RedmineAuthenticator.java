package com.example.diti.redminemobileclient.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.activities.LoginActivity;
import com.example.diti.redminemobileclient.model.Users;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;


public class RedmineAuthenticator extends AbstractAccountAuthenticator {
    private final static String TAG = "RedmineAuthenticator";
    Context mContext;

    public RedmineAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {
        final AccountManager am = AccountManager.get(mContext);

        //String authToken = am.peekAuthToken(account, authTokenType);
        String       authToken = am.getPassword(account);
        final Bundle result    = new Bundle();

        //если токен не закэшировался при создании аккаунта - запрашиваем токен опять по логину паролю
        if (TextUtils.isEmpty(authToken)) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext
                                                                                        .getString(
                                                                                                R.string.preference_file_key),
                                                                                Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(mContext.getString(R.string.settings_base_url), am.getUserData
                    (account, mContext.getString(R.string.AM_BASE_URL)));
            editor.commit();

            try {
                RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(
                        am.getPassword(account), mContext);
                Call<Users> call =
                        client.reposForUser();

                Response<Users> execute = call.execute();
                if (execute.isSuccessful()) {
                    authToken = execute.body().getUser().getApiKey();
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                    result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    am.setAuthToken(account, account.type, authToken);
                } else {
                    final Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                    result.putParcelable(AccountManager.KEY_INTENT, intent);
                    result.putString(AccountManager.KEY_ERROR_MESSAGE, "Ошибка авторизации");
                }
            } catch (IOException | NullPointerException e) {
                Log.e(TAG, e.getLocalizedMessage());
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "Не удалось получить токен. " +
                        ""+e.getLocalizedMessage());
            }
        }

        //Фантастика, токен закешировался, возвращаем его
        else {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        }

        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }
}
