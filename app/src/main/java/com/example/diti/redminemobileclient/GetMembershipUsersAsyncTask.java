package com.example.diti.redminemobileclient;

import android.support.v4.app.DialogFragment;
import android.os.AsyncTask;
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
    private String                           token;
    private TaskDelegate                     mDelegate;
    private String                           projectId;
    private DialogFragment                   mDialog;
    private List<MembershipUser>             mCheckedUsers;

    public interface TaskDelegate {
        public void removeProgressBar();
    }

    public GetMembershipUsersAsyncTask(AppCompatActivity context, String authToken, String id, DialogFragment dialog, List<MembershipUser> checkedUsers) {
        contextRef = new WeakReference<AppCompatActivity>(context);
        token = authToken;
        mDelegate = (TaskDelegate) contextRef.get();
        projectId = id;
        mDialog = dialog;
        mCheckedUsers = checkedUsers;
    }

    protected List<Membership> doInBackground(Void... voids) {
        List<Membership> userMembershipList = new ArrayList<>();
        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(token, null, contextRef
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
        //AssignedToListDialog dialog = new AssignedToListDialog();
        if (mDialog instanceof AssignedToListDialog) {
            AssignedToListDialog mD = (AssignedToListDialog) mDialog;
            mD.show(contextRef.get()
                            .getSupportFragmentManager(), "AssignedToListDialog", memberships);
        }
        if (mDialog instanceof SupervisorListDialog) {
            SupervisorListDialog mD = (SupervisorListDialog) mDialog;
            mD.show(contextRef.get()
                            .getSupportFragmentManager(), "SupervisorListDialog", memberships, mCheckedUsers);
        }
        mDelegate.removeProgressBar();
    }
}
