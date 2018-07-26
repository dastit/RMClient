package com.example.diti.redminemobileclient.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.IssueViewModel;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.IssueJournal;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TaskCommentsFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    public IssueViewModel mIssueViewModel;

    public TaskCommentsFragment() {
    }


    public static TaskCommentsFragment newInstance() {
        TaskCommentsFragment fragment = new TaskCommentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_task_comment_list, container, false);
        mIssueViewModel = ViewModelProviders.of(getActivity()).get(IssueViewModel.class);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mIssueViewModel.getIssueLiveData().observe(getActivity(), new Observer<Issue>() {
                @Override
                public void onChanged(@Nullable Issue issue) {
                    if(issue !=null){
                        recyclerView.setAdapter(new TaskCommentsFragmentAdapter(issue.getJournals(), mListener));
                    }
                }
            });
        }
        return view;
    }

    public class TaskCommentsFragmentAdapter extends RecyclerView.Adapter<TaskCommentsFragmentAdapter.ViewHolder> {

        private final List<IssueJournal>                   mValues;
        private final OnListFragmentInteractionListener mListener;

        public TaskCommentsFragmentAdapter(List<IssueJournal> items, OnListFragmentInteractionListener listener) {
            mValues = items;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_task_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.bindItem(mValues.get(position));

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View      mView;
            public final TextView  mContentView;
            public       IssueJournal mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.note);
            }

            public void bindItem (IssueJournal journal){
                mContentView.setText(journal.getNotes());
            }
            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                       + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction();
    }
}
