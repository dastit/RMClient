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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.SwipeController;
import com.example.diti.redminemobileclient.SwipeControllerActions;
import com.example.diti.redminemobileclient.activities.MainActivity;
import com.example.diti.redminemobileclient.activities.TaskActivity;
import com.example.diti.redminemobileclient.datasources.PagedTaskListRepository;
import com.example.diti.redminemobileclient.datasources.PagedTasksListViewModel;
import com.example.diti.redminemobileclient.datasources.PagedTasksListViewModelFactory;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public TaskListFragment() {
    }

    public static TaskListFragment newInstance(String token) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAuthToken = getArguments().getString(ARG_TOKEN);

        RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient(mAuthToken, "");
        repository = new PagedTaskListRepository(client);
        PagedTasksListViewModelFactory factory = new PagedTasksListViewModelFactory(repository);
        viewModel = ViewModelProviders.of(this, factory).get(PagedTasksListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasklist_list, container, false);

        // Set the adapter and swipecontroller
        Context context = view.getContext();

        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                repository.getDSFactory().postLiveData.getValue().invalidate();
            }
        });

        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
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
                Toast.makeText(getActivity(), "нажата кнопка " + position, Toast.LENGTH_LONG)
                        .show();
                Issue mIssue = mAdapter.getItemClicked(position);
                createNotification(mIssue);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                notificationManager.notify(111, notification);
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


        return view;
    }

    private void createNotification(Issue mIssue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID).setSmallIcon(R.drawable.outline_timer_24)
                    .setContentTitle("Выполняется задача №" + mIssue.getIssueid())
                    .setContentText(mIssue.getSubject());
        } else {
            notificationBuilder = new NotificationCompat.Builder(getActivity()).setSmallIcon(R.drawable.outline_timer_24)
                    .setContentTitle("Выполняется задача №" + mIssue.getIssueid())
                    .setContentText(mIssue.getSubject());
        }

        Intent playIntent = new Intent(getActivity(), MainActivity.class);
        playIntent.setAction(ACTION_STOP);
        PendingIntent pendingPlayIntent = PendingIntent.getActivity(getActivity(), 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(R.drawable.outline_stop_24, "Stop", pendingPlayIntent);
        notificationBuilder.addAction(playAction);

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
        void onTaskListFragmentInteraction();
    }

    //класс адаптера
    public class TaskListAdapter extends PagedListAdapter<Issue, TaskListAdapter.TaskListViewHolder> {

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

        public Issue getItemClicked(int position) {
            return getItem(position);
        }


        public class TaskListViewHolder extends RecyclerView.ViewHolder {
            public TextView  mTaskSubject;
            public TextView  mTaskCreationDate;
            public TextView  mTaskProject;
            public ImageView mProjectFirstLetterImageView;
            public TextView  mProjectFirstLetterTextView;


            public TaskListViewHolder(View itemView) {
                super(itemView);

                mTaskSubject = itemView.findViewById(R.id.task_subject);
                mTaskCreationDate = itemView.findViewById(R.id.task_date);
                mTaskProject = itemView.findViewById(R.id.task_project);
                int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                mTaskProject.setMaxWidth(screenWidth / 3);
                mProjectFirstLetterImageView = itemView.findViewById(R.id.project_letter_image_view);
                mProjectFirstLetterTextView = itemView.findViewById(R.id.project_letter_text_view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mAdapter.getCurrentList()!= null){
                            Intent intent = new Intent(getActivity(), TaskActivity.class);
                            String issueIds = "";
                            for (int i = 0; i<mAdapter.getCurrentList().size(); i++) {
                                Issue issue = mAdapter.getCurrentList().get(i);
                                if(issue!=null){
                                    issueIds = issue.getIssueid()+":"+issueIds;
                                }
                            }
                            intent.putExtra(TaskActivity.EXTRA_ISSUES_LIST, issueIds);
                            intent.putExtra(TaskActivity.EXTRA_TOKEN, mAuthToken);
                            startActivity(intent);
                        }
                    }
                });
            }

            public void bind(Issue issue) {
                if (issue == null) {
                    mTaskSubject.setText(R.string.waiting_for_information);
                    mTaskCreationDate.setText("");
                    mTaskProject.setText("");
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    Date date;
                    Calendar cal = Calendar.getInstance();
                    try {
                        date = formatter.parse(issue.getCreatedOn().replaceAll("Z$", "+0000"));
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
