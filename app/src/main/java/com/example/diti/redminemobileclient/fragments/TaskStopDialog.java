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

public class TaskStopDialog extends DialogFragment {

    private static final String TAG ="TaskStopDialog" ;
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
        long time = sharedPreferences.getLong(getString(R.string.task_time_started_key), System.currentTimeMillis());
        long estimatedTime = System.currentTimeMillis() - time;
        double seconds = Math.floor(estimatedTime / 1000);
        int hours = (int) Math.floor(Math.floor(seconds / 60) / 60);
        int minutes = (int) (Math.floor(seconds / 60) - hours * 60);

        String message = getString(R.string.stop_task_message);
        if(hours!=0){
            String hoursWord = " часов ";
            if(hours==1){
                hoursWord = " час ";
            }
            if(hours==2 || hours==3 || hours == 4){
                hoursWord = " часа ";
            }
            message = message+hours+hoursWord;
        }
        String minWord = " минут";
        if(minutes==1){
            minWord = " минута";
        }
        if(minutes==2 || minutes==3 || minutes == 4){
            minWord = " минуты";
        }
        message = message + minutes+minWord;
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
        if (context instanceof OnDialogIterationListener) {
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
