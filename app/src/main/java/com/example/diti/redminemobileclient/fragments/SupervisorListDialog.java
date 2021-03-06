package com.example.diti.redminemobileclient.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.diti.redminemobileclient.model.Membership;
import com.example.diti.redminemobileclient.model.MembershipUser;

import java.util.ArrayList;
import java.util.List;


public class SupervisorListDialog extends DialogFragment {
    private static final String TAG = "SupervisorListDialog";
    private List<Membership> mMemberships;
    private ArrayMap<Integer, String> chosen    = new ArrayMap<>();
    private SupervisorListDialogListener mListener;

    public interface SupervisorListDialogListener {
        public void onItemClick(ArrayMap<Integer, String>  users);
    }

    public void show(FragmentManager manager, String tag, List<Membership> memberships, ArrayMap<Integer, String> checkedUsers) {
        mMemberships = memberships;
        chosen = checkedUsers;
        super.show(manager, tag);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (SupervisorListDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                                         + " must implement SupervisorListDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean[] checkedItems = new boolean[mMemberships.size()];
        CharSequence[] names = new CharSequence[mMemberships.size()];
        int i = 0;
        for (Membership membership : mMemberships) {
            names[i] = membership.getUser().getName();
            checkedItems[i] = false;
            if(chosen.containsKey(membership.getUser().getId())){
                checkedItems[i] = true;
            }
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setMultiChoiceItems(names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                MembershipUser user = mMemberships.get(which).getUser();
                if (isChecked) {
                    chosen.put(user.getId(), user.getName());
                } else if (chosen.containsKey(user.getId())) {
                    chosen.remove(user.getId());
                }
            }
        }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mListener.onItemClick(chosen);
            }
        }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onItemClick(chosen);
            }
        });
        return builder.create();
    }
}
