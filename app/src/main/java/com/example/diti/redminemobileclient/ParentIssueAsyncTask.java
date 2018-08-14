package com.example.diti.redminemobileclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.example.diti.redminemobileclient.fragments.ParentIssuesDialog;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ParentIssueAsyncTask extends AsyncTask <Void, Void, List<Issue>> {
    private WeakReference<AppCompatActivity>     contextRef;
    private String                               token;
    private TaskDelegate mDelegate;
    private String                               projectId;
    private boolean flag;

    public interface TaskDelegate {
        public void removeProgressBar();
        public void checkInsertedIssueId(List<Issue> issues);
    }

    public ParentIssueAsyncTask(AppCompatActivity context, String authToken, String id, boolean openDialogFlag) {
        contextRef = new WeakReference<AppCompatActivity>(context);
        token = authToken;
        mDelegate = (TaskDelegate) contextRef.get();
        projectId = id;
        flag = openDialogFlag;
    }

    protected List<Issue> doInBackground(Void... voids) {
        List<Issue> issues = new ArrayList<>();
        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(token, null, contextRef
                .get()
                .getCacheDir());
        Call<Issues> call = client.reposForTasksInProject(projectId);
        try {
            Response<Issues> response = call.execute();
            issues = response.body().getIssues();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return issues;
    }

    @Override
    protected void onPostExecute(List<Issue> issues) {
        if(flag == true){
            ParentIssuesDialog dialog = new ParentIssuesDialog();
            dialog.show(contextRef.get().getSupportFragmentManager(), "ParentIssuesDialog", issues);
            mDelegate.removeProgressBar();
        }
       else{
            mDelegate.checkInsertedIssueId(issues);
        }
    }
}

