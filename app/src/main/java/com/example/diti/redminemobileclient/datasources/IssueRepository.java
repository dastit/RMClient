package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;

import retrofit2.Call;

public class IssueRepository {
    private static final String TAG ="IssueRepository" ;
    private final RedmineRestApiClient.RedmineClient mRedmineClient;
    private final IssueDao                           mIssueDao;
    private final Executor                           executor;

    public IssueRepository(RedmineRestApiClient.RedmineClient mRedmineClient, IssueDao mIssueDao, Executor executor) {
        this.mRedmineClient = mRedmineClient;
        this.mIssueDao = mIssueDao;
        this.executor = executor;
    }

    public LiveData<Issue> getIssue(String issueId) {
        refreshUser(issueId);
        // return a LiveData directly from the database.
        return mIssueDao.load(issueId);
    }

    private void refreshUser(final String issueId) {
        Integer issueExists = mIssueDao.hasIssue(issueId, new Date().getTime());
            if (issueExists == 0) {
                Log.d(TAG, "Need new one");
                Issue issue = null;
                Call<Issue> call = mRedmineClient.reposForTask(issueId);
                try {
                    issue = call.execute().body();
                    issue.setLastUpdate(new Date().getTime());
                    mIssueDao.save(issue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.d(TAG, "Refreshed found");
            }
    }

}
