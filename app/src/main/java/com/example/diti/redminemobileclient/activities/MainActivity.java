package com.example.diti.redminemobileclient.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diti.redminemobileclient.DateConverter;
import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.authenticator.RedmineAccount;
import com.example.diti.redminemobileclient.fragments.AccountListFragment;
import com.example.diti.redminemobileclient.fragments.ProjectListFragment;
import com.example.diti.redminemobileclient.fragments.TaskListFragment;
import com.example.diti.redminemobileclient.fragments.TaskStopDialog;
import com.example.diti.redminemobileclient.model.Issue;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AccountListFragment.AccountListListener, TaskListFragment.OnListFragmentInteractionListener, ProjectListFragment.OnListFragmentInteractionListener, TaskStopDialog.OnDialogIterationListener {
    private static final String TAG                       = "MainActivity";
    private static final String STATE_AUTH_TOKEN          = "state_auth_token";
    private static final String PROJECT_LIST_FRAGMENT_TAG = "projectList";
    public static final String  TASK_LIST_FRAGMENT_TAG    = "taskList";

    private AccountManager      mAccountManager;
    private AccountListFragment mAccountListFragment;
    private DrawerLayout        mDrawerLayout;
    private ProgressBar         mProgressView;
    private Toolbar             topToolbar;
    private ActionBar           ab;
    private String              authToken;
    private CardView mFreezedIssue;

    private boolean isAuthNeeded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbars(savedInstanceState);
        mProgressView = findViewById(R.id.central_fragment_progress);
        mFreezedIssue = (CardView)findViewById(R.id.freezed_issue);


        if (savedInstanceState != null) {
            Log.d(TAG, "Restore state");
            authToken = savedInstanceState.getString(STATE_AUTH_TOKEN);
            if (authToken != null) {
                isAuthNeeded = false;
            }
        }

        if (isAuthNeeded == true) {
            mProgressView.setVisibility(View.VISIBLE);
            mAccountManager = AccountManager.get(this);
            int accNum = mAccountManager.getAccounts().length;
            //если это первый вход в систему - предлагаем залогиниться
            if (accNum == 0) {
                addNewAccount();
            }
            //если аккаунт один - автоматический вход в систему
            else if (accNum == 1) {
                getToken(mAccountManager.getAccounts()[0]);
            }
            //если аккаунтов много - предлагаем выбрать аккаунт
            else {
                CharSequence[] names = new CharSequence[mAccountManager.getAccounts().length];
                int i = 0;
                for (Account account : mAccountManager.getAccounts()) {
                    names[i] = account.name;
                    i++;
                }
                mAccountListFragment = AccountListFragment.newInstance(names);
                mAccountListFragment.show(getSupportFragmentManager(), "AccountListFragment");
            }

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
        mAccountManager.addAccount(RedmineAccount.TYPE, RedmineAccount.TOKEN_FULL_ACCESS, null, null, this,
                                   new AccountManagerCallback<Bundle>() {
                                       @Override
                                       public void run(AccountManagerFuture<Bundle> future) {
                                           try {
                                               Bundle result = future.getResult();
                                               authToken = result.getString(AccountManager.KEY_PASSWORD);
                                               initCentralFragment(authToken);
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
                                                 authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                                                 mAccountManager.invalidateAuthToken(account.type, authToken);
                                                 Log.d(TAG, "Token invalidated");
                                                 if (future.isDone() && !future.isCancelled()) {
                                                     initCentralFragment(authToken);
                                                 }
                                             } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                                 Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                                                         .show();
                                                 addNewAccount();
                                             }
                                         }
                                     }, null
        );
    }


    private void initToolbars(final Bundle savedInstanceState) {
        topToolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(topToolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ab.setHomeAsUpIndicator(R.drawable.outline_menu_24);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

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
                                            .replace(R.id.fragment_container, taskListFragment, TASK_LIST_FRAGMENT_TAG)
                                            .commit();

                                case R.id.menu_projects://пункт меню "Проекты"
                                    ProjectListFragment projectListFragment = (ProjectListFragment) getSupportFragmentManager()
                                            .findFragmentByTag(PROJECT_LIST_FRAGMENT_TAG);
                                    if (projectListFragment == null) {
                                        projectListFragment = ProjectListFragment.newInstance(authToken);
                                    }
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, projectListFragment, PROJECT_LIST_FRAGMENT_TAG)
                                            .commit();
                                    mFreezedIssue.setVisibility(View.GONE);

                                case R.id.menu_stat://пункт меню "Статистика"

                            }
                        }
                        return true;
                    }
                });
    }

    private void initCentralFragment(String token) {

        if (findViewById(R.id.fragment_container) != null) {
            TaskListFragment taskListFragment = TaskListFragment.newInstance(token);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, taskListFragment, TASK_LIST_FRAGMENT_TAG)
                    .commit();
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

    //методы для фрагмента TaskListFragment
    @Override
    public void showFreezedTask(Issue issue) {
        mFreezedIssue.setVisibility(View.VISIBLE);
        TextView mTaskSubject = (TextView)findViewById(R.id.f_task_subject);
        TextView  mTaskCreationDate = (TextView)findViewById(R.id.f_task_date);
        TextView  mTaskProject = (TextView)findViewById(R.id.f_task_project);
        TextView  mProjectFirstLetterTextView = (TextView)findViewById(R.id.f_project_letter_text_view);
        TextView  mTaskId = (TextView)findViewById(R.id.f_task_id);
        ImageButton mStopTimerButton = (ImageButton)findViewById(R.id.f_stop_timer_button);

        mStopTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskStopDialog dialog  = new TaskStopDialog();
                dialog.show(getSupportFragmentManager(), "TaskStopDialog");
            }
        });
        mTaskCreationDate.setText(DateConverter.getDate(issue.getCreatedOn()));
        mFreezedIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                Integer issueId = issue.getIssueid();
                intent.putExtra(TaskActivity.EXTRA_ISSUE_ID, issueId);
                intent.putExtra(TaskActivity.EXTRA_TOKEN, authToken);
                startActivity(intent);
            }
        });
        mTaskId.setText("# " + issue.getIssueid().toString());
        mTaskSubject.setText(issue.getSubject());
        mTaskProject.setText(issue.getProject().getName());
        String projectName = issue.getProject().getName().substring(0, 1);
        mProjectFirstLetterTextView.setText(projectName);
    }

    @Override
    public void invalidateFreezedTask() {
        mFreezedIssue.invalidate();
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
        mFreezedIssue.setVisibility(View.GONE);
        TaskListFragment taskListFragment = TaskListFragment.newInstance(authToken);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, taskListFragment, TASK_LIST_FRAGMENT_TAG)
                .commitNow();
    }
}

