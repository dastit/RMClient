package com.example.diti.redminemobileclient.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diti.redminemobileclient.DateConverter;
import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.SwipeController;
import com.example.diti.redminemobileclient.SwipeControllerActions;
import com.example.diti.redminemobileclient.activities.NewTaskActivity;
import com.example.diti.redminemobileclient.activities.TaskActivity;
import com.example.diti.redminemobileclient.datasources.PagedTaskListRepository;
import com.example.diti.redminemobileclient.datasources.PagedTasksListViewModel;
import com.example.diti.redminemobileclient.datasources.PagedTasksListViewModelFactory;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

public class TaskListFragment extends Fragment {

    private static final String ARG_TOKEN        = "token";
    public static final  String EXTRA_ISSUE_NUM  = "issue_num";
    public static final  String EXTRA_ISSUE_SUBJ = "issue_subj";
    private static final String CHANNEL_ID       = "CHANNEL_ID_FOR_TASK_EXEC";
    public static final  String ACTION_STOP      = "ACTION_STOP";


    private String                            mAuthToken;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView                      mRecyclerView;
    private TaskListAdapter                   mAdapter;
    private RecyclerView.LayoutManager        mLayoutManager;
    public  PagedTasksListViewModel           viewModel;
    private ItemTouchHelper                   itemTouchHelper;
    private NotificationCompat.Builder        notificationBuilder;
    private Notification                      notification;
    private PagedTaskListRepository           repository;
    private ProgressBar                       mProgressBar;
    private SharedPreferences                 mSharedPreferences;
    private FloatingActionButton              mCreateNewTaskButton;
    private SharedPreferences.Editor          mEditor;
    private TextView mErrorText;

    public TaskListFragment() {
    }

    public static TaskListFragment newInstance(String token) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle           args     = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAuthToken = getArguments().getString(ARG_TOKEN);

        mSharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasklist_list, container, false);
        try {
            RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(
                    mAuthToken, getActivity());
        repository = new PagedTaskListRepository(client);
        PagedTasksListViewModelFactory factory = new PagedTasksListViewModelFactory(repository);
        viewModel = ViewModelProviders.of(this, factory).get(PagedTasksListViewModel.class);

        // Set the adapter and swipecontroller

        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                repository.getDSFactory().postLiveData.getValue().invalidate();
            }
        });
        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        Context context = view.getContext();
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TaskListAdapter();
        viewModel.pagedList.observe(getActivity(), new Observer<PagedList<Issue>>() {
            @Override
            public void onChanged(@Nullable PagedList<Issue> issues) {
                swipeContainer.setRefreshing(false);
                mAdapter.submitList(issues);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        final SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Issue   mIssue  = mAdapter.getItemClicked(position);
                Context context = getActivity().getApplicationContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences(
                        getString(R.string.preference_file_key), context.MODE_PRIVATE);
                if (sharedPreferences.contains(getString(R.string.task_id_started_key)) &&
                        sharedPreferences.getInt(getString(R.string.task_id_started_key), 0) !=
                                mIssue.getIssueid()) {
                    Toast.makeText(getActivity(), "Закончите выполнение задачи №" +
                            sharedPreferences.getInt(getString(R.string.task_id_started_key), 0) +
                            " прежде чем запускать новую", Toast.LENGTH_LONG)
                         .show();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(getString(R.string.task_id_started_key), mIssue.getIssueid());
                    editor.putLong(getString(R.string.task_time_started_key),
                                   System.currentTimeMillis());
                    editor.commit();
                    createNotification(mIssue);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                            getActivity());
                    notificationManager.notify(111, notification);
                    if (mListener != null) {
                        mListener.invalidateFreezedTask();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, getActivity());
        itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        mCreateNewTaskButton = (FloatingActionButton) view.findViewById(R.id.new_task_fab);
        mCreateNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
                intent.putExtra(NewTaskActivity.EXTRA_AUTH, mAuthToken);
                startActivity(intent);
            }
        });
        }catch (NullPointerException e){
            mListener.setProgressBar();
            mErrorText = view.findViewById(R.id.error_text);
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(R.string.empty_base_url_error);
        }
        return view;
    }

    private void createNotification(Issue mIssue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence        name        = getString(R.string.channel_name);
            String              description = getString(R.string.channel_description);
            int                 importance  = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel     = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(
                    NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(getActivity(),
                                                                 CHANNEL_ID).setSmallIcon(
                    R.drawable.outline_timer_24)
                                                                            .setContentTitle(
                                                                                    "Выполняется задача №" + mIssue
                                                                                            .getIssueid())
                                                                            .setContentText(
                                                                                    mIssue.getSubject());
        } else {
            notificationBuilder = new NotificationCompat.Builder(getActivity()).setSmallIcon(
                    R.drawable.outline_timer_24)
                                                                               .setContentTitle(
                                                                                       "Выполняется задача №" + mIssue
                                                                                               .getIssueid())
                                                                               .setContentText(
                                                                                       mIssue.getSubject());
        }

        Intent playIntent = new Intent(getActivity(), TaskActivity.class);
        playIntent.putExtra(TaskActivity.EXTRA_ISSUE_ID, mIssue.getIssueid());
        playIntent.putExtra(TaskActivity.EXTRA_TOKEN, mAuthToken);
        playIntent.putExtra(TaskActivity.IS_TASK_STOPED_FROM_NOTIFICATION, true);
        playIntent.setAction(ACTION_STOP);
        PendingIntent pendingPlayIntent = PendingIntent.getActivity(getActivity(), 0,
                                                                    playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(
                R.drawable.outline_stop_24, "Stop", pendingPlayIntent);
        notificationBuilder.addAction(playAction);
        notificationBuilder.setAutoCancel(true);

        notification = notificationBuilder.build();
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

    //интерфейс взаимодействия с хостовой активностью
    public interface OnListFragmentInteractionListener {
        void invalidateFreezedTask();

        void setProgressBar();
    }

    //класс адаптера
    public class TaskListAdapter
            extends PagedListAdapter<Issue, TaskListAdapter.TaskListViewHolder> {

        protected TaskListAdapter() {
            super(DIFF_CALLBACK);
        }

        @NonNull
        @Override
        public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity())
                                   .inflate(R.layout.fragment_tasklist, parent, false);
            TaskListViewHolder vh = new TaskListViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
            holder.bind(getItem(position));
        }

        @Override
        public void onViewAttachedToWindow(@NonNull TaskListViewHolder holder) {
            if (mListener != null) {
                mListener.setProgressBar();
            }
        }

        public Issue getItemClicked(int position) {

            return getItem(position);
        }


        public class TaskListViewHolder extends RecyclerView.ViewHolder {
            public TextView  mTaskSubject;
            public TextView  mTaskCreationDate;
            public TextView  mTaskProject;
            public ImageView mProjectFirstLetterImageView;
            public TextView  mProjectFirstLetterTextView;
            public TextView  mTaskId;


            public TaskListViewHolder(View itemView) {
                super(itemView);

                mTaskSubject = itemView.findViewById(R.id.task_subject);
                mTaskCreationDate = itemView.findViewById(R.id.task_date);
                mTaskProject = itemView.findViewById(R.id.task_project);
                int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                mTaskProject.setMaxWidth(screenWidth / 3);
                mProjectFirstLetterImageView = itemView.findViewById(
                        R.id.project_letter_image_view);
                mProjectFirstLetterTextView = itemView.findViewById(R.id.project_letter_text_view);
                mTaskId = itemView.findViewById(R.id.task_id);
            }

            public void bind(Issue issue) {

                if (issue == null) {
                    mTaskSubject.setText(R.string.waiting_for_information);
                    mTaskCreationDate.setText("");
                    mTaskProject.setText("");
                } else {
                    if (issue.getIssueid() ==
                            mSharedPreferences.getInt(getString(R.string.task_id_started_key), 0)) {
                        itemView.setVisibility(View.GONE);
                        itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        return;
                    }
                    mTaskCreationDate.setText(DateConverter.getDate(issue.getCreatedOn()));
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent  intent  = new Intent(getActivity(), TaskActivity.class);
                            Integer issueId = issue.getIssueid();
                            intent.putExtra(TaskActivity.EXTRA_ISSUE_ID, issueId);
                            intent.putExtra(TaskActivity.EXTRA_TOKEN, mAuthToken);
                            startActivity(intent);
                        }
                    });
                    mTaskId.setText("# " + issue.getIssueid().toString());
                    mTaskSubject.setText(issue.getSubject());
                    mTaskProject.setText(issue.getProject().getName());
                    String projectName = issue.getProject().getName().substring(0, 1);
                    mProjectFirstLetterTextView.setText(projectName);
                }
            }
        }
    }

    public DiffUtil.ItemCallback<Issue> DIFF_CALLBACK = new DiffUtil.ItemCallback<Issue>() {

        @Override
        public boolean areItemsTheSame(Issue oldItem, Issue newItem) {
            return oldItem.getSubject().equals(newItem.getSubject());
        }

        @Override
        public boolean areContentsTheSame(Issue oldItem, Issue newItem) {
            return oldItem.getDescription().equals(newItem.getDescription());
        }
    };
}
