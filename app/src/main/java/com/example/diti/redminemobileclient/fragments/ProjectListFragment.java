package com.example.diti.redminemobileclient.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.PagedProjectListViewModel;
import com.example.diti.redminemobileclient.datasources.PagedProjectListViewModelFactory;

import com.example.diti.redminemobileclient.datasources.PagedProjectListRepository;
import com.example.diti.redminemobileclient.model.Project;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ProjectListFragment extends Fragment {

    private static final String ARG_TOKEN = "token";
    private OnListFragmentInteractionListener mListener;

    private     String                           mAuthToken;
    public      PagedProjectListViewModel        viewModel;
    private RecyclerView                     mRecyclerView;
    private     ProjectListAdapter mAdapter;
    private     RecyclerView.LayoutManager       mLayoutManager;


    public ProjectListFragment() {
    }


    public static ProjectListFragment newInstance(String token) {
        ProjectListFragment fragment = new ProjectListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mAuthToken = getArguments().getString(ARG_TOKEN);
        }
        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(mAuthToken, "");
        PagedProjectListRepository mPagedProjectListRepository = new PagedProjectListRepository(client);
        PagedProjectListViewModelFactory factory = new PagedProjectListViewModelFactory(mPagedProjectListRepository);
        viewModel = ViewModelProviders.of(this, factory).get(PagedProjectListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasklist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new ProjectListAdapter();
            viewModel.pagedList.observe(getActivity(), new Observer<PagedList<Project>>() {
                @Override
                public void onChanged(@Nullable PagedList<Project> projects) {
                    mAdapter.submitList(projects);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
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
        void onProjectListFragmentInteraction();
    }


    public class ProjectListAdapter extends PagedListAdapter<Project, ProjectListFragment.ProjectListAdapter.ViewHolder> {

        public ProjectListAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.fragment_tasklist, parent, false);
            ProjectListFragment.ProjectListAdapter.ViewHolder vh = new ProjectListFragment.ProjectListAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.bind(getItem(position));
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTaskSubject;
            public TextView mTaskCreationDate;
            public TextView mTaskProject;

            public ViewHolder(View itemView) {
                super(itemView);
                mTaskSubject = itemView.findViewById(R.id.task_subject);
                mTaskCreationDate = itemView.findViewById(R.id.task_date);
                mTaskProject = itemView.findViewById(R.id.task_project);
                int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                mTaskProject.setMaxWidth(screenWidth / 3);

            }

            public void bind(Project project) {
                if (project == null) {
                    mTaskSubject.setText("R.string.waiting_for_information");
                    mTaskCreationDate.setText("");
                    mTaskProject.setText("");
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    Date date;
                    Calendar cal = Calendar.getInstance();
                    try {
                        date = formatter.parse(project.getCreatedOn().replaceAll("Z$", "+0000"));
                        cal.setTime(date);
                        int day = cal.get(Calendar.DATE);
                        int month = cal.get(Calendar.MONTH);
                        int year = cal.get(Calendar.YEAR);
                        int hours = cal.get(Calendar.HOUR_OF_DAY);
                        int minutes = cal.get(Calendar.MINUTE);
                        mTaskCreationDate.setText(
                                day + "." + month + "." + year + " " + hours + ":" + minutes);
                    } catch (ParseException e) {
                        date = new Date();
                        e.printStackTrace();
                    }
                    mTaskSubject.setText(project.getName());
                    mTaskProject.setText("");

                }
            }
        }
    }

    public DiffUtil.ItemCallback<Project> DIFF_CALLBACK = new DiffUtil.ItemCallback<Project>() {

        @Override
        public boolean areItemsTheSame(Project oldItem, Project newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(Project oldItem, Project newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };
}
