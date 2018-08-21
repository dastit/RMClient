package com.example.diti.redminemobileclient;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.diti.redminemobileclient.fragments.ProjectListDialog;
import com.example.diti.redminemobileclient.model.Project;
import com.example.diti.redminemobileclient.model.Projects;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProjectListAsyncTask extends AsyncTask<Void, Void, List<Project>> {
    private WeakReference<AppCompatActivity> contextRef;
    private String                           token;
    private TaskDelegate mDelegate;

    public interface TaskDelegate {
        public void removeProgressBar();
    }

    public ProjectListAsyncTask(AppCompatActivity context, String authToken, TaskDelegate delegate) {
        contextRef = new WeakReference<AppCompatActivity>(context);
        token = authToken;
        mDelegate = delegate;
    }

    protected List<Project> doInBackground(Void... voids) {
        List<Project> projectList = new ArrayList<>();
        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(token, null, contextRef.get().getCacheDir());
        Call<Projects> call = client.reposForProjects(0, 25, "name");
        try {
            Response<Projects> response = call.execute();
            projectList = response.body().getProjects();
            int offset = 25;
            int limit = 25;
            int totalCount = response.body().getTotalCount();
            //TODO: DO NOT DELETE COMMENTED - commented for debug
//            while (offset+limit <= totalCount){
//                call = client.reposForProjects(offset, limit, "name");
//                response =  call.execute();
//                projectList.addAll(response.body().getProjects());
//                offset =response.body().getOffset()+limit;
//            }
        } catch (IOException | NullPointerException e) {
            Toast.makeText(contextRef.get(), R.string.connection_mistake, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return projectList;
    }

    @Override
    protected void onPostExecute(List<Project> projects) {
        ProjectListDialog dialog = new ProjectListDialog();
        dialog.show(contextRef.get().getSupportFragmentManager(), "ProjectListDialog", projects);
        mDelegate.removeProgressBar();
    }

}
