package com.example.diti.redminemobileclient.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.IssueDatabase;
import com.example.diti.redminemobileclient.datasources.IssueRepository;
import com.example.diti.redminemobileclient.datasources.IssueViewModel;
import com.example.diti.redminemobileclient.datasources.IssueViewModelFactory;
import com.example.diti.redminemobileclient.fragments.TaskCommentsFragment;
import com.example.diti.redminemobileclient.fragments.TaskDetailsFragment;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;


public class TaskActivity extends AppCompatActivity implements TaskDetailsFragment.OnFragmentInteractionListener, TaskCommentsFragment.OnListFragmentInteractionListener {

    private static final String TAG            = "TaskActivity";
    public static final  String EXTRA_ISSUE_ID = "issue_id";
    public static final  String EXTRA_TOKEN    = "token";

    private ViewPager              mViewPager;
    private MyFragmentPagerAdapter mAdapter;
    private String                 mAuthToken;
    private IssueViewModel  mIssueViewModel;
    private IssueDatabase   mDatabase;
    int pages = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Integer issueId = getIntent().getIntExtra(EXTRA_ISSUE_ID, 0);
        mAuthToken = getIntent().getStringExtra(EXTRA_TOKEN);

        mDatabase = Room.databaseBuilder(getApplicationContext(), IssueDatabase.class, "issue").fallbackToDestructiveMigration().build();
        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(mAuthToken, "");
        IssueRepository repository = new IssueRepository(client, mDatabase.mIssueDao(), this);
        IssueViewModelFactory factory = new IssueViewModelFactory(repository);
        mIssueViewModel = ViewModelProviders.of(this, factory).get(IssueViewModel.class);

        mViewPager = (ViewPager) findViewById(R.id.tasks_pager);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... ids) {
                mIssueViewModel.init(ids[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mViewPager.setAdapter(mAdapter);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }.execute(issueId);
    }



    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return TaskDetailsFragment.newInstance();
            }
            else
            {
                return TaskCommentsFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return pages;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return getString(R.string.task_details_tab);
            }
            else {
                return getString(R.string.task_comments_tab);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction() {

    }
}
