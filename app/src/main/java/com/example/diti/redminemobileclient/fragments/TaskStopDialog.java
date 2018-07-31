package com.example.diti.redminemobileclient.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.activities.MainActivity;

import java.util.Calendar;
import java.util.Date;

public class TaskStopDialog extends DialogFragment {

    private OnDialogIterationListener mListener;

    public TaskStopDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Context context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(getString(R.string.task_time_started_key), 0);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime() - time);
        String message = getString(R.string.stop_task_message)+cal.get(Calendar.HOUR)+" часов "+cal.get(Calendar.MINUTE)+" минут.";
        builder.setMessage(message)
                .setPositiveButton(R.string.stop_task, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                        notificationManager.cancel(111);
                        notificationManager.cancel(000);
                        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), getActivity().getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(getString(R.string.task_id_started_key));
                        editor.remove(getString(R.string.task_time_started_key));
                        editor.commit();
                        if (mListener != null) {
                            mListener.OnDialogIteration();
                        }
                    }
                })
                .setNegativeButton(R.string.dont_stop_task, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context.getClass().equals(MainActivity.class)){
           mListener = null;
        }
        else if (context instanceof OnDialogIterationListener) {
            mListener = (OnDialogIterationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                       + " must implement OnDialogIterationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDialogIterationListener {
        void OnDialogIteration();
    }
}
