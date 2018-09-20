package com.example.diti.redminemobileclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.example.diti.redminemobileclient.model.Membership;
import com.example.diti.redminemobileclient.model.Memberships;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class GetMembershipUsersAsyncTask extends AsyncTask<Void, Void, ArrayList<Membership>> {
    private static final String TAG = "GetMembershipUsersATask";
    private WeakReference<AppCompatActivity> contextRef;
    private String                           token;
    private TaskDelegate                     mDelegate;
    private String                           projectId;

    public interface TaskDelegate {
        void saveMembersList(ArrayList<Membership> memberships);
    }

    public GetMembershipUsersAsyncTask(AppCompatActivity context, String authToken,
                                       String projectId) {
        contextRef = new WeakReference<AppCompatActivity>(context);
        token = authToken;
        mDelegate = (TaskDelegate) contextRef.get();
        this.projectId = projectId;
    }

    protected ArrayList<Membership> doInBackground(Void... voids) {
        ArrayList<Membership> userMembershipList = new ArrayList<>();
        try {
            RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(token,
                                                                                              contextRef
                                                                                                      .get());
            Call<Memberships> call = client.reposForMembership(projectId);

            Response<Memberships> response = call.execute();
            userMembershipList = response.body().getMemberships();
        } catch (IOException |NullPointerException e) {
            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(contextRef.get(), R.string.connection_mistake, Toast.LENGTH_LONG).show();
        }
        return userMembershipList;
    }

    @Override
    protected void onPostExecute(ArrayList<Membership> memberships) {
        mDelegate.saveMembersList(memberships);
    }
}
