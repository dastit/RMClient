package com.example.diti.redminemobileclient.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.IssueDatabase;
import com.example.diti.redminemobileclient.datasources.IssueRepository;
import com.example.diti.redminemobileclient.datasources.IssueViewModel;
import com.example.diti.redminemobileclient.model.Issue;


public class TaskFragment extends Fragment {

    private static final String ARG_ISSUE = "issue";
    private static final String ARG_TOKEN = "token";

    private String                        mIssue;
    private TextView                      mTextView;
    private OnFragmentInteractionListener mListener;
    private IssueRepository               mIssueRepository;
    private IssueViewModel                mIssueViewModel;
    private IssueDatabase mDatabase;
    private String mAuthToken;

    public TaskFragment() {
    }

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mIssueViewModel = ViewModelProviders.of(getActivity()).get(IssueViewModel.class);
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        mTextView = (TextView)v.findViewById(R.id.task_subj);
        mIssueViewModel.getIssueLiveData().observe(getActivity(), new Observer<Issue>() {
            @Override
            public void onChanged(@Nullable Issue issue) {
                if(issue !=null){
                    String d = issue.getDescription();
                    mTextView.setText(d);
                }
            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                       + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
