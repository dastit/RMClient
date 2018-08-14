package com.example.diti.redminemobileclient.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.example.diti.redminemobileclient.model.Project;

import java.util.List;

public class ProjectListDialog extends DialogFragment {
    private AlertDialog.Builder builder;
    private List<Project> mProjectList;
    private ProjectListDialogListener mListener;

    public interface ProjectListDialogListener {
        public void onItemClick(Project project);
    }

    public void show(FragmentManager manager, String tag, List<Project> projects) {
        mProjectList = projects;
        super.show(manager, tag);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (ProjectListDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                                         + " must implement ProjectListDialogListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence[] names = new CharSequence[mProjectList.size()];
        int i=0;
        for (Project project:mProjectList) {
            names[i] = project.getName();
            i++;
        }
        builder = new AlertDialog.Builder(getActivity()).setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onItemClick(mProjectList.get(which));
            }
        });
        return builder.create();
    }


}
