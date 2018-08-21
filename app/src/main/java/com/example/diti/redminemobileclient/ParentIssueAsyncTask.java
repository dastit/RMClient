package com.example.diti.redminemobileclient;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ParentIssueAsyncTask extends AsyncTask<Void, Void, List<Issue>> {
    private WeakReference<AppCompatActivity> contextRef;
    private String token;
    private TaskDelegate mDelegate;
    private String projectId;

    public interface TaskDelegate {
        void createAdaptedForParentIssues(List<Issue> issues);
    }

    public ParentIssueAsyncTask(AppCompatActivity context, String authToken, String id) {
        contextRef = new WeakReference<AppCompatActivity>(context);
        token = authToken;
        mDelegate = (TaskDelegate) contextRef.get();
        projectId = id;
    }

    protected List<Issue> doInBackground(Void... voids) {
        List<Issue> issues = new ArrayList<>();
        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(token, null, contextRef
                .get()
                .getCacheDir());
        Call<Issues> call = client.reposForTasksInProject(projectId, 0, 25, "name");
        try {
            Response<Issues> response = call.execute();
            issues = response.body().getIssues();

            //TODO:DO NOT DELETE COMMENTED - commented for debug
//            int offset = 25;
//            int limit = 25;
//            int totalCount = response.body().getTotalCount();
//            while(offset+limit < totalCount){
//                call = client.reposForTasksInProject(projectId, offset, limit, "name");
//                response = call.execute();
//                issues.addAll(response.body().getIssues());
//                offset = offset+limit;
//            }

        } catch (IOException | NullPointerException e) {
            Toast.makeText(contextRef.get(), contextRef.get().getString(R.string.connection_mistake), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return issues;
    }

    @Override
    protected void onPostExecute(List<Issue> issues) {
        mDelegate.createAdaptedForParentIssues(issues);
    }
}

