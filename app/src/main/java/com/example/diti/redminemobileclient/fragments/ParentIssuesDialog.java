package com.example.diti.redminemobileclient.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.example.diti.redminemobileclient.model.Issue;

import java.util.List;

public class ParentIssuesDialog extends DialogFragment {
    private List<Issue>               mIssueList;
    private ParenIssuesDialogListener mListener;

    public interface ParenIssuesDialogListener {
        public void onItemClick(Issue issue);
    }

    public void show(FragmentManager manager, String tag, List<Issue> issues) {
        mIssueList = issues;
        super.show(manager, tag);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (ParenIssuesDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                                         + " must implement ParenIssuesDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence[] ids = new CharSequence[mIssueList.size()];
        int i = 0;
        for (Issue issue : mIssueList) {
            ids[i] = "#"+String.valueOf(issue.getIssueid())+": "+issue.getSubject();
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setItems(ids, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onItemClick(mIssueList.get(which));
            }
        });
        return builder.create();
    }
}
