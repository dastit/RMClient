package com.example.diti.redminemobileclient.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.IssueDatabase;
import com.example.diti.redminemobileclient.datasources.IssueRepository;
import com.example.diti.redminemobileclient.datasources.IssueViewModel;
import com.example.diti.redminemobileclient.datasources.IssueViewModelFactory;
import com.example.diti.redminemobileclient.fragments.NewCommentDialog;
import com.example.diti.redminemobileclient.fragments.TaskCommentsFragment;
import com.example.diti.redminemobileclient.fragments.TaskDetailsFragment;
import com.example.diti.redminemobileclient.fragments.TaskStopDialog;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.IssueAttachment;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.File;
import java.util.List;
import java.util.Random;


public class TaskActivity extends AppCompatActivity
        implements TaskDetailsFragment.OnFragmentInteractionListener,
                   TaskCommentsFragment.OnListFragmentInteractionListener,
                   TaskStopDialog.OnDialogIterationListener {

    private static final String TAG                              = "TaskActivity";
    public static final  String EXTRA_ISSUE_ID                   = "issue_id";
    public static final  String EXTRA_TOKEN                      = "token";
    public static final  String IS_TASK_STOPED_FROM_NOTIFICATION = "is_task_stoped_from_notification";

    private static final String CHANNEL_ID  = "CHANNEL_ID_FOR_TASK_EXEC";
    public static final  String ACTION_STOP = "ACTION_STOP";


    private ViewPager              mViewPager;
    private MyFragmentPagerAdapter mAdapter;
    private String                 mAuthToken;
    private IssueViewModel         mIssueViewModel;
    private SharedPreferences      sharedPreferences;
    private ProgressBar            mProgressBar;
    private Integer                issueId;
    private Issue                  mIssue;
    private String                 uniqueIdPrefix;

    private RedmineRestApiClient.RedmineClient client;
    private IssueRepository                    repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        mProgressBar = findViewById(R.id.task_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        issueId = getIntent().getIntExtra(EXTRA_ISSUE_ID, 0);
        mAuthToken = getIntent().getStringExtra(EXTRA_TOKEN);
        boolean isStopedFromNotificaton = getIntent().getBooleanExtra(
                IS_TASK_STOPED_FROM_NOTIFICATION,
                false);

        IssueDatabase mDatabase = Room.databaseBuilder(getApplicationContext(), IssueDatabase.class,
                                                       "issue")
                                      .fallbackToDestructiveMigration()
                                      .allowMainThreadQueries()
                                      .build();
        try {
            client = RedmineRestApiClient.getRedmineClient(mAuthToken, this);
            repository = new IssueRepository(client, mDatabase.mIssueDao(), this);
            mIssueViewModel = ViewModelProviders.of(this, new IssueViewModelFactory(repository))
                                                .get(IssueViewModel.class);

            mViewPager = findViewById(R.id.tasks_pager);
            mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);

            Toolbar topToolbar = findViewById(R.id.task_top_toolbar);
            setSupportActionBar(topToolbar);
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }

            mIssueViewModel.getStoredIssue(issueId);
            mIssueViewModel.getIssueLiveData().observe(this, new Observer<Issue>() {
                @Override
                public void onChanged(@Nullable Issue issue) {
                    if (repository.isExpired(issue)) {
                        mIssueViewModel.requestNewIssueData(issueId);
                    }
                }
            });

            if (isStopedFromNotificaton) {
                startDialog();
            }
        } catch (NullPointerException e) {
            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(this, R.string.empty_base_url_error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_top_toolbar_menu, menu);

        if (mIssue != null && mIssue.getAttachments().size() != 0) {
            Random rand = new Random();
            uniqueIdPrefix = String.valueOf(rand.nextInt(89) + 10);
            List<IssueAttachment> attachmentsList = mIssue.getAttachments();
            MenuItem              item            = menu.findItem(R.id.task_attachments);
            SubMenu               attachments     = item.getSubMenu();
            attachments.clear();
            for (int i = 0; i < attachmentsList.size(); i++) {
                int itemId = Integer.parseInt(uniqueIdPrefix + 1);
                attachments.add(0, itemId, 0, attachmentsList.get(i).getFilename());
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.task_start_timer:
                sharedPreferences = getSharedPreferences(
                        getString(R.string.preference_file_key),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.task_id_started_key), issueId);
                editor.putLong(getString(R.string.task_time_started_key),
                               System.currentTimeMillis());
                editor.apply();
                createNotification(issueId);
                invalidateOptionsMenu();
                return true;
            case R.id.task_stop_timer:
                startDialog();
                return true;
            default:
                if (String.valueOf(item.getItemId()).startsWith(String.valueOf(uniqueIdPrefix))) {
                    File   file   = new File(getCacheDir(), item.getTitle().toString());
                    Uri    uri    = FileProvider.getUriForFile(this, "be.myapplication", file);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    MimeTypeMap myMime = MimeTypeMap.getSingleton();
                    String mimeType = myMime.getMimeTypeFromExtension(
                            file.getName().substring(file.getName().lastIndexOf(".") + 1));
                    intent.setDataAndType(uri, mimeType);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "Для файла не найдено соответствующее приложение",
                                       Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }
        }
    }

    private void createNotification(Integer issueId) {
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence        name        = getString(R.string.channel_name);
            String              description = getString(R.string.channel_description);
            int                 importance  = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel     = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(
                    R.drawable.outline_timer_24)
                                                                                  .setContentTitle(
                                                                                          "Выполняется задача №" + issueId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(
                    R.drawable.outline_timer_24)
                                                                      .setContentTitle(
                                                                              "Выполняется задача №" + issueId);
        }

        Intent playIntent = new Intent(this, TaskActivity.class);
        playIntent.putExtra(TaskActivity.EXTRA_ISSUE_ID, issueId);
        playIntent.putExtra(TaskActivity.EXTRA_TOKEN, mAuthToken);
        playIntent.putExtra(TaskActivity.IS_TASK_STOPED_FROM_NOTIFICATION, true);
        playIntent.setAction(ACTION_STOP);
        PendingIntent pendingPlayIntent = PendingIntent.getActivity(this, 0, playIntent,
                                                                    0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(
                R.drawable.outline_stop_24, "Stop", pendingPlayIntent);
        notificationBuilder.addAction(playAction);
        notificationBuilder.setAutoCancel(true);

        Notification              notification        = notificationBuilder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(000, notification);
    }

    public void startDialog() {
        TaskStopDialog dialog = new TaskStopDialog();
        dialog.show(getSupportFragmentManager(), "TaskStopDialog");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);
        if (sharedPreferences.getInt(getString(R.string.task_id_started_key), 0) == issueId) {
            menu.findItem(R.id.task_start_timer).setVisible(false);
            menu.findItem(R.id.task_stop_timer).setVisible(true);
        } else {
            menu.findItem(R.id.task_start_timer).setVisible(true);
            menu.findItem(R.id.task_stop_timer).setVisible(false);
        }
        return true;
    }

    @Override
    public void OnDialogIteration() {

        invalidateOptionsMenu();
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return TaskDetailsFragment.newInstance();
            } else {
                return TaskCommentsFragment.newInstance(mAuthToken);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.task_details_tab);
            } else {
                return getString(R.string.task_comments_tab);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Issue issue) {
        mIssue = issue;
        invalidateOptionsMenu();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onListFragmentInteraction() {

    }
}
