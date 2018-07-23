package com.example.diti.redminemobileclient.datasources;

import android.util.Log;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;

import retrofit2.Call;

public class IssueRepository {
    private static final String TAG ="IssueRepository" ;
    private final RedmineRestApiClient.RedmineClient mRedmineClient;
    private final IssueDao                           mIssueDao;

    public IssueRepository(RedmineRestApiClient.RedmineClient mRedmineClient, IssueDao mIssueDao) {
        this.mRedmineClient = mRedmineClient;
        this.mIssueDao = mIssueDao;
    }

    public Issue getIssue(Integer issueId) {
        Issue issue = mIssueDao.load(issueId);

        if (isExpired(issue)) {
            Log.d(TAG, "Need new one");
            Call<IssueResponse> call = mRedmineClient.reposForTask(issueId.toString());
            try {
                IssueResponse issueResponse = call.execute().body();
                issue = issueResponse.getIssue();
                issue.setLast_request_time_in_milliseconds(System.currentTimeMillis());

                mIssueDao.save(issue);

                issue = mIssueDao.load(issueId);
                Log.d(TAG, String.valueOf(issue == null));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d(TAG, "Refreshed found");
        }
        return issue;
    }

    private boolean isExpired(Issue issue) {
        if(issue == null || issue.getLast_request_time_in_milliseconds() == 0 || issue.getLast_request_time_in_milliseconds() < System.currentTimeMillis()- (10 * 60 * 1000)){
           return true;
        }
        return false;
    }

}
