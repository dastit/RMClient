package com.example.diti.redminemobileclient.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.diti.redminemobileclient.datasources.IssueViewModelFactory;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    private static final String ARG_ISSUE = "issue";
    private static final String ARG_TOKEN = "token";

    private String                        mIssueID;
    private TextView                      mTextView;
    private OnFragmentInteractionListener mListener;
    private IssueRepository               mIssueRepository;
    private IssueViewModel                mIssueViewModel;
    private IssueDatabase mDatabase;
    private String mAuthToken;

    public TaskFragment() {
    }

    public static TaskFragment newInstance(String issueId, String token) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ISSUE, issueId);
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIssueID = getArguments().getString(ARG_ISSUE);
            mAuthToken = getArguments().getString(ARG_TOKEN);


            mDatabase = Room.inMemoryDatabaseBuilder(getActivity(), IssueDatabase.class).build();
            RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(mAuthToken, "");
            IssueRepository repository = new IssueRepository(client, mDatabase.mIssueDao(), Executors
                    .newSingleThreadExecutor());
            IssueViewModelFactory factory = new IssueViewModelFactory(repository);
            mIssueViewModel = ViewModelProviders.of(this, factory).get(IssueViewModel.class);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task, container, false);

        mTextView = (TextView)v.findViewById(R.id.task_subj);
        new MyTask().execute(mIssueID);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class MyTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... id) {
            mIssueViewModel.init(id[0]);

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mTextView.setText("Данные загружаются");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mTextView.setText(mIssueViewModel.getIssueLiveData().getValue().getDescription());
            mIssueViewModel.getIssueLiveData().observeForever(new Observer<Issue>() {
                @Override
                public void onChanged(@Nullable Issue issue) {
                    if(issue !=null){
                        mTextView.setText(issue.getDescription());
                    }
                }
            });

        }
    }
}
