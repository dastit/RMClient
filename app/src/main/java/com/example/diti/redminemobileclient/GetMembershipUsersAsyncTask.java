package com.example.diti.redminemobileclient;

import android.support.v4.app.DialogFragment;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import com.example.diti.redminemobileclient.fragments.AssignedToListDialog;
import com.example.diti.redminemobileclient.fragments.SupervisorListDialog;
import com.example.diti.redminemobileclient.model.Membership;
import com.example.diti.redminemobileclient.model.MembershipUser;
import com.example.diti.redminemobileclient.model.Memberships;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GetMembershipUsersAsyncTask extends AsyncTask<Void, Void, List<Membership>> {
    private WeakReference<AppCompatActivity> contextRef;
    private String token;
    private TaskDelegate mDelegate;
    private String projectId;

    public interface TaskDelegate {
        void saveMembersList(List<Membership> memberships);
    }

    public GetMembershipUsersAsyncTask(AppCompatActivity context, String authToken,
                                       String projectId) {
        contextRef = new WeakReference<AppCompatActivity>(context);
        token = authToken;
        mDelegate = (TaskDelegate) contextRef.get();
        this.projectId = projectId;
    }

    protected List<Membership> doInBackground(Void... voids) {
        List<Membership> userMembershipList = new ArrayList<>();
        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(token,
                                                                                          null,
                                                                                          contextRef
                                                                                                  .get()
                                                                                                  .getCacheDir());
        Call<Memberships> call = client.reposForMembership(projectId);
        try {
            Response<Memberships> response = call.execute();
            userMembershipList = response.body().getMemberships();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userMembershipList;
    }

    @Override
    protected void onPostExecute(List<Membership> memberships) {
        mDelegate.saveMembersList(memberships);
    }
}
