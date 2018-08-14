package com.example.diti.redminemobileclient.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.diti.redminemobileclient.model.Membership;

import java.util.List;

public class AssignedToListDialog extends DialogFragment {

    private List<Membership> mMemberships;
    private AssignedToListDialogListener mListener;

    public interface AssignedToListDialogListener {
        public void onItemClick(Membership membership);
    }

    public void show(FragmentManager manager, String tag, List<Membership> memberships) {
        mMemberships = memberships;
        super.show(manager, tag);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (AssignedToListDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                                         + " must implement AssignedToListDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence[] names = new CharSequence[mMemberships.size()];
        int i=0;
        for (Membership membership:mMemberships) {
            names[i] = membership.getUser().getName();
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onItemClick(mMemberships.get(which));
            }
        });
        return builder.create();
    }
}
