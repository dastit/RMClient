package com.example.diti.redminemobileclient.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diti.redminemobileclient.DateConverter;
import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.authenticator.RedmineAccount;
import com.example.diti.redminemobileclient.datasources.IssueResponse;
import com.example.diti.redminemobileclient.fragments.AccountListFragment;
import com.example.diti.redminemobileclient.fragments.NoConnectionDialog;
import com.example.diti.redminemobileclient.fragments.ProjectListFragment;
import com.example.diti.redminemobileclient.fragments.TaskListFragment;
import com.example.diti.redminemobileclient.fragments.TaskStopDialog;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements AccountListFragment.AccountListListener,
                   TaskListFragment.OnListFragmentInteractionListener,
                   ProjectListFragment.OnListFragmentInteractionListener,
                   TaskStopDialog.OnDialogIterationListener {
    private static final String TAG                       = "MainActivity";
    private static final String STATE_AUTH_TOKEN          = "state_auth_token";
    private static final String PROJECT_LIST_FRAGMENT_TAG = "projectList";
    public static final  String TASK_LIST_FRAGMENT_TAG    = "taskList";

    private AccountManager mAccountManager;
    private DrawerLayout   mDrawerLayout;
    private ProgressBar    mProgressView;
    private String         authToken;
    private CardView       mFriezedIssue;

    private ImageView mUserAvatarImageView;
    private TextView  mUserNameTextView;
    private TextView  mUserLoginTextView;

    private String mLogin;
    private String mUserName;
    private String mEmail;

    private boolean isAuthNeeded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbars();
        mProgressView = findViewById(R.id.central_fragment_progress);
        mFriezedIssue = findViewById(R.id.freezed_issue);

        if (savedInstanceState != null) {
            Log.d(TAG, "Restore state");
            authToken = savedInstanceState.getString(STATE_AUTH_TOKEN);
            if (authToken != null) {
                isAuthNeeded = false;
                //initCentralFragment(authToken);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            if (isAuthNeeded) {
                mProgressView.setVisibility(View.VISIBLE);
                mAccountManager = AccountManager.get(this);
                int accNum = mAccountManager.getAccounts().length;
                //если это первый вход в систему - предлагаем залогиниться
                if (accNum == 0) {
                    addNewAccount();
                }
                //если аккаунт один - автоматический вход в систему
                else if (accNum == 1) {
                    Account account = mAccountManager.getAccounts()[0];
                    getToken(account);
                }
                //если аккаунтов много - предлагаем выбрать аккаунт
                else {
                    CharSequence[] names = new CharSequence[mAccountManager.getAccounts().length];
                    int            i     = 0;
                    for (Account account : mAccountManager.getAccounts()) {
                        names[i] = account.name;
                        i++;
                    }
                    AccountListFragment mAccountListFragment = AccountListFragment.newInstance(
                            names);
                    mAccountListFragment.show(getSupportFragmentManager(), "AccountListFragment");
                }
            } else {
                initCentralFragment();
            }
        } else {
            NoConnectionDialog dialog = new NoConnectionDialog();
            dialog.show(getFragmentManager(), "NoConnectionDialog");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_AUTH_TOKEN, authToken);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_top_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewAccount() {
        mAccountManager.addAccount(RedmineAccount.TYPE, RedmineAccount.TOKEN_FULL_ACCESS, null,
                                   null, this,
                                   new AccountManagerCallback<Bundle>() {
                                       @Override
                                       public void run(AccountManagerFuture<Bundle> future) {
                                           try {
                                               Bundle result = future.getResult();
                                               mEmail = result.getString(
                                                       getString(R.string.AM_EMAIL));

                                               mLogin = result.getString(
                                                       AccountManager.KEY_ACCOUNT_NAME);
                                               mUserName = result.getString(getString(R.string
                                                                                              .AM_FIO));
                                               authToken = result.getString(
                                                       AccountManager.KEY_PASSWORD);
                                               initCentralFragment();
                                           } catch (OperationCanceledException e) {
                                               e.printStackTrace();
                                           } catch (IOException e) {
                                               e.printStackTrace();
                                           } catch (AuthenticatorException e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }, null);
    }

    public void getToken(final Account account) {

        mAccountManager.getAuthToken(account, RedmineAccount.TOKEN_FULL_ACCESS, new Bundle(), true,
                                     new AccountManagerCallback<Bundle>() {
                                         @Override
                                         public void run(AccountManagerFuture<Bundle> future) {
                                             try {
                                                 Bundle result = future.getResult();
                                                 Intent intent = result.getParcelable(AccountManager
                                                                                              .KEY_INTENT);
                                                 if (intent != null) {
                                                     Toast.makeText(MainActivity.this, intent
                                                                            .getStringExtra(AccountManager
                                                                                                    .KEY_ERROR_MESSAGE),
                                                                    Toast.LENGTH_LONG).show();
                                                 }
                                                 authToken = result.getString(
                                                         AccountManager.KEY_AUTHTOKEN);
                                                 mEmail = result.getString(
                                                         getString(R.string.AM_EMAIL));

                                                 mLogin = account.name;
                                                 mUserName = result.getString(getString(R.string
                                                                                                .AM_FIO));
                                                 mAccountManager.invalidateAuthToken(account.type,
                                                                                     authToken);
                                                 Log.d(TAG, "Token invalidated");
                                                 if (future.isDone() && !future.isCancelled()) {
                                                     String baseUrl = mAccountManager.getUserData
                                                             (account,
                                                              getString(R.string.AM_BASE_URL));
                                                     SharedPreferences sharedPreferences =
                                                             getSharedPreferences(getString(R
                                                                                                    .string.preference_file_key),
                                                                                  MODE_PRIVATE);
                                                     SharedPreferences.Editor editor =
                                                             sharedPreferences.edit();
                                                     editor.putString(getString(R.string
                                                                                        .settings_base_url),
                                                                      baseUrl);
                                                     editor.apply();
                                                     initCentralFragment();
                                                 }
                                             } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                                 Toast.makeText(getApplicationContext(),
                                                                e.getMessage(), Toast.LENGTH_LONG)
                                                      .show();
                                                 addNewAccount();
                                             }
                                         }
                                     }, null
        );
    }


    private void initToolbars() {
        Toolbar topToolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(topToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.outline_menu_24);
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //TODO:create feedback
                        return false;
                    }
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (findViewById(R.id.fragment_container) != null) {
                            switch (item.getItemId()) {
                                case R.id.menu_tasks: //пункт меню "Задачи"
                                    TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager()
                                            .findFragmentByTag(TASK_LIST_FRAGMENT_TAG);
                                    if (taskListFragment == null) {
                                        taskListFragment = TaskListFragment.newInstance(authToken);
                                    }
                                    getSupportFragmentManager().beginTransaction()
                                                               .replace(R.id.fragment_container,
                                                                        taskListFragment,
                                                                        TASK_LIST_FRAGMENT_TAG)
                                                               .commit();

                                case R.id.menu_projects://пункт меню "Проекты"
                                    ProjectListFragment projectListFragment = (ProjectListFragment) getSupportFragmentManager()
                                            .findFragmentByTag(PROJECT_LIST_FRAGMENT_TAG);
                                    if (projectListFragment == null) {
                                        projectListFragment = ProjectListFragment.newInstance(
                                                authToken);
                                    }
                                    getSupportFragmentManager().beginTransaction()
                                                               .replace(R.id.fragment_container,
                                                                        projectListFragment,
                                                                        PROJECT_LIST_FRAGMENT_TAG)
                                                               .commit();
                                    mFriezedIssue.setVisibility(View.GONE);

                                case R.id.menu_stat://пункт меню "Статистика"
                            }
                        }
                        return true;
                    }
                });
    }

    private void initCentralFragment() {
        fillNavigationDrawerHeader();
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            try {
                if (findViewById(R.id.fragment_container) != null) {
                    TaskListFragment taskListFragment = TaskListFragment.newInstance(authToken);
                    getSupportFragmentManager().beginTransaction()
                                               .add(R.id.fragment_container, taskListFragment,
                                                    TASK_LIST_FRAGMENT_TAG)
                                               .commit();
                }
                SharedPreferences sharedPreferences = getSharedPreferences(
                        getString(R.string.preference_file_key), MODE_PRIVATE);
                if (sharedPreferences.contains(getString(R.string.task_id_started_key))) {
                    showFreezedTask(sharedPreferences.getInt(getString(R.string
                                                                               .task_id_started_key),
                                                             0));
                }
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Не удалось загрузить данные, пожалуйста, перезапустите " +
                        "приложение.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void fillNavigationDrawerHeader() {
        mUserAvatarImageView = findViewById(R.id.nav_header_avatar_image);
        mUserNameTextView = findViewById(R.id.nav_header_user_name);
        mUserLoginTextView = findViewById(R.id.nav_header_user_login);

        if (mEmail != null) {
            mEmail = mEmail.trim().toLowerCase();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(mEmail.getBytes());
                byte messageDigest[] = md.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    String h = Integer.toHexString(0xFF & aMessageDigest);
                    while (h.length() < 2)
                        h = "0" + h;
                    hexString.append(h);
                }
                String url = "https://www.gravatar.com/avatar/" + hexString.toString() +
                        "?default=wavatar&s=120";
                Picasso.get().load(url).into(mUserAvatarImageView);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    //методы для фрагмента AccountListFragment
    @Override
    public void onItemClick(DialogFragment dialog, int which) {
        getToken(mAccountManager.getAccounts()[which]);
    }

    @Override
    public void onButtonClick(DialogFragment dialog) {
        addNewAccount();
    }

    public void showFreezedTask(int issueId) {
        try {
            RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient
                    (authToken, this);
            Call<IssueResponse> call = client.reposForTask(String.valueOf(issueId));
            call.enqueue(new Callback<IssueResponse>() {
                @Override
                public void onResponse(@NonNull Call<IssueResponse> call,
                                       @NonNull Response<IssueResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body()
                                                                                      .getIssue() != null) {
                        Issue issue = response.body().getIssue();
                        mFriezedIssue.setVisibility(View.VISIBLE);
                        TextView mTaskSubject      = findViewById(R.id.f_task_subject);
                        TextView mTaskCreationDate = findViewById(R.id.f_task_date);
                        TextView mTaskProject      = findViewById(R.id.f_task_project);
                        TextView mProjectFirstLetterTextView = findViewById(
                                R.id.f_project_letter_text_view);
                        TextView mTaskId = findViewById(R.id.f_task_id);
                        ImageButton mStopTimerButton = findViewById(
                                R.id.f_stop_timer_button);

                        mStopTimerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TaskStopDialog dialog = new TaskStopDialog();
                                dialog.show(getSupportFragmentManager(), "TaskStopDialog");
                            }
                        });
                        mTaskCreationDate.setText(DateConverter.getDate(issue.getCreatedOn()));
                        mFriezedIssue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                                intent.putExtra(TaskActivity.EXTRA_ISSUE_ID, issueId);
                                intent.putExtra(TaskActivity.EXTRA_TOKEN, authToken);
                                startActivity(intent);
                            }
                        });
                        mTaskId.setText(String.format("# %s", String.valueOf(issueId)));
                        mTaskSubject.setText(issue.getSubject());
                        mTaskProject.setText(issue.getProject().getName());
                        String projectName = issue.getProject().getName().substring(0, 1);
                        mProjectFirstLetterTextView.setText(projectName);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IssueResponse> call, @NonNull Throwable t) {

                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, e.getLocalizedMessage());
            TextView errorText = findViewById(R.id.f_task_subject);
            errorText.setText(e.getLocalizedMessage());
        }
    }

    @Override
    public void invalidateFreezedTask() {
        mFriezedIssue.invalidate();
    }

    @Override
    public void setProgressBar() {
        mProgressView.setVisibility(View.GONE);
    }

    //методы для фрагмента ProjectListFragment
    @Override
    public void onProjectListFragmentInteraction() {
    }

    //методы для TaskStopDialog
    @Override
    public void OnDialogIteration() {
        mFriezedIssue.setVisibility(View.GONE);
        TaskListFragment taskListFragment = TaskListFragment.newInstance(authToken);
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, taskListFragment,
                                            TASK_LIST_FRAGMENT_TAG)
                                   .commitNow();
    }
}

