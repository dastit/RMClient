package com.example.diti.redminemobileclient.activities;

import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.diti.redminemobileclient.R;


public class TaskActivity extends AppCompatActivity implements TaskFragment.OnFragmentInteractionListener {

    private static final String TAG               = "TaskActivity";
    public static final  String EXTRA_ISSUES_LIST = "issues_list";
    public static final String EXTRA_TOKEN = "token";

    private ViewPager              mViewPager;
    private MyFragmentPagerAdapter mAdapter;
    private String mAuthToken;
    private int PAGE_COUNT = 10;
    private String[] mIssueList = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        String issueIds = getIntent().getStringExtra(EXTRA_ISSUES_LIST);
        mIssueList = issueIds.split(":", 0);
        mAuthToken = getIntent().getStringExtra(EXTRA_TOKEN);

        mViewPager = (ViewPager) findViewById(R.id.tasks_pager);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TaskFragment.newInstance(mIssueList[position], mAuthToken);
        }

        @Override
        public int getCount() {
            return mIssueList.length;
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
