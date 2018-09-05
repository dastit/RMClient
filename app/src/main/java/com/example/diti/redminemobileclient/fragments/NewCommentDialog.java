package com.example.diti.redminemobileclient.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.diti.redminemobileclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNewCommentDialogInteractionListener} interface
 * to handle interaction events.
 */
public class NewCommentDialog extends DialogFragment {
    public EditText comment;

    private OnNewCommentDialogInteractionListener mListener;

    public NewCommentDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        comment= new EditText(getActivity());
        comment.setMinLines(10);
        comment.setRawInputType(EditorInfo.TYPE_CLASS_TEXT);

        return builder.setView(comment).setPositiveButton(android.R.string.ok,
                                                   new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialogInterface,
                                                                           int i) {
                                                           String commentText = comment.getText().toString();
                                                           mListener.addNewComment //TODO: addreal functionality
                                                                   (commentText);
                                                       }
                                                   }).setNegativeButton(R.string.dont_stop_task,
                                                                        null).create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewCommentDialogInteractionListener) {
            mListener = (OnNewCommentDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                               + " must implement OnNewCommentDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNewCommentDialogInteractionListener {
        void addNewComment(String commentText);
    }
}
