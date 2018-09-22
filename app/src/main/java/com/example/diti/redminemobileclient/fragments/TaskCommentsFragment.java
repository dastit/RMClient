package com.example.diti.redminemobileclient.fragments;

import android.accounts.AccountManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diti.redminemobileclient.DateConverter;
import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.IssueResponse;
import com.example.diti.redminemobileclient.datasources.IssueViewModel;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.IssueJournal;
import com.example.diti.redminemobileclient.model.IssueJournalUser;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TaskCommentsFragment extends Fragment {
    public static final  String     TAG          = "TaskCommentsFragment";
    private static final int        REQUEST_CODE = 0;
    private static final DateFormat sdf          = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final String ARGS_AUTHTOKEN      = "auth_token";

    private OnListFragmentInteractionListener mListener;
    public  IssueViewModel                    mIssueViewModel;
    private RecyclerView                      recyclerView;
    private FloatingActionButton              mNewCommentButton;

    private String mAuthToken;
    private String mIssueId;

    public TaskCommentsFragment() {
    }


    public static TaskCommentsFragment newInstance(String token) {
        TaskCommentsFragment fragment = new TaskCommentsFragment();
        Bundle               args     = new Bundle();
        args.putString(ARGS_AUTHTOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthToken = getArguments().getString(ARGS_AUTHTOKEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Создание фрагмента с комментариями");
        View view = inflater.inflate(R.layout.fragment_task_comment_list, container, false);
        mIssueViewModel = ViewModelProviders.of(getActivity()).get(IssueViewModel.class);

        // Set the adapter
        recyclerView = (RecyclerView) view.findViewById(R.id.comment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        mIssueViewModel.getIssueLiveData().observe(getActivity(), new Observer<Issue>() {
            @Override
            public void onChanged(@Nullable Issue issue) {
                if (issue != null) {
                    mIssueId = String.valueOf(issue.getIssueid());
                    recyclerView.setAdapter(
                            new TaskCommentsFragmentAdapter(issue.getJournals(), mListener, issue));
                }
            }
        });

        // Set floating button
        mNewCommentButton = (FloatingActionButton) view.findViewById(R.id.add_comment_fab);
        mNewCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewCommentDialog dialog = new NewCommentDialog();
                dialog.setTargetFragment(TaskCommentsFragment.this, REQUEST_CODE);
                dialog.show(getFragmentManager(), "NewCommentDialog");
            }
        });
        return view;
    }

    public class TaskCommentsFragmentAdapter
            extends RecyclerView.Adapter<TaskCommentsFragmentAdapter.ViewHolder> {

        private final List<IssueJournal>                mValues;
        private final OnListFragmentInteractionListener mListener;
        private       Issue                             mIssue;

        public TaskCommentsFragmentAdapter(List<IssueJournal> items,
                                           OnListFragmentInteractionListener listener,
                                           Issue issue) {
            mValues = items;
            mListener = listener;
            mIssue = issue;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.fragment_task_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.bindItem(mValues.get(position), mIssue);
        }

        public void addItem(IssueJournal journal){
            mValues.add(journal);
            notifyItemInserted(getItemCount());
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View         mView;
            public final TextView     mNote;
            public final TextView     mAuthor;
            public final TextView     mDate;
            public final ImageButton  mExpandButton;
            public final ImageButton  mCollapseButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNote = (TextView) view.findViewById(R.id.task_comment_note);
                mAuthor = (TextView) view.findViewById(R.id.task_comment_author);
                mDate = (TextView) view.findViewById(R.id.task_comment_date);
                mExpandButton = (ImageButton) view.findViewById(R.id.task_comment_expand_button);
                mCollapseButton = (ImageButton) view.findViewById(
                        R.id.task_comment_collapse_button);
            }

            public void bindItem(IssueJournal journal, Issue issue) {
                mNote.setText(journal.getNotes());
                mAuthor.setText(journal.getUser().getName());
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mCollapseButton.getVisibility() == View.GONE) {
                            mNote.setMaxLines(1000);
                            mCollapseButton.setVisibility(View.VISIBLE);
                            mExpandButton.setVisibility(View.GONE);
                        } else if (mCollapseButton.getVisibility() == View.VISIBLE) {
                            mNote.setMaxLines(3);
                            mCollapseButton.setVisibility(View.GONE);
                            mExpandButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

                mDate.setText(DateConverter.getDate(journal.getCreatedOn()));

                if (issue.getAttachments().size() != 0) {
                    new AsyncTask<Issue, Void, SpannableStringBuilder>() {
                        @Override
                        protected SpannableStringBuilder doInBackground(Issue... issues) {
                            String                 text = journal.getNotes();
                            SpannableStringBuilder ssb  = new SpannableStringBuilder(text);
                            for (int i = 0; i < issues[0].getAttachments().size(); i++) {
                                String name = issues[0].getAttachments().get(i).getFilename();
                                if (text.contains(name)) {
                                    File file;
                                    Uri  uri;
                                    try {
                                        file = new File(getActivity().getCacheDir(), name);
                                        uri = FileProvider.getUriForFile(getActivity(),
                                                                         "be.myapplication", file);
                                    } catch (NullPointerException npe) {
                                        getActivity().finish();
                                        break;
                                    }
                                    int firstIndex = text.indexOf(name) -
                                            1;        //единички учитывают восклицательные знаки
                                    int lastIndex = firstIndex + name.length() +
                                            1; //по обеим сторонам от названия картинки
                                    ImageSpan imageSpan = null;
                                    Bitmap    bitmap    = null;
                                    try {
                                        bitmap = Picasso.get().load(uri).get();
                                        DisplayMetrics displayMetrics = new DisplayMetrics();
                                        getActivity().getWindowManager()
                                                     .getDefaultDisplay()
                                                     .getMetrics(displayMetrics);
                                        int width = Math.min(displayMetrics.widthPixels,
                                                             bitmap.getWidth());
                                        bitmap = Bitmap.createScaledBitmap(bitmap, width,
                                                                           bitmap.getHeight(),
                                                                           true);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    imageSpan = new ImageSpan(getActivity(), bitmap);
                                    ssb.setSpan(imageSpan, firstIndex, lastIndex,
                                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                            }
                            return ssb;
                        }

                        @Override
                        protected void onPostExecute(
                                SpannableStringBuilder spannableStringBuilder) {
                            mNote.setText(spannableStringBuilder);
                        }
                    }.execute(issue);
                }
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNote.getText() + "'";
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
        void onListFragmentInteraction();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE){
            String commentText = data.getStringExtra(NewCommentDialog.EXTRA_COMMENT_TEXT);

            Issue    issue         = new Issue();
            IssueResponse issueResponse = new IssueResponse();
            issue.setNote(commentText);
            issueResponse.setIssue(issue);
            try {

                RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient
                        (mAuthToken, getActivity());

                Call<ResponseBody> call = client.sendNewComment(mIssueId, issueResponse);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call,
                                           @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            //mIssueViewModel.requestNewIssueData(Integer.valueOf(mIssueId));
                            IssueJournal issueJournal = new IssueJournal();
                            issueJournal.setNotes(commentText);
                            AccountManager   am   = AccountManager.get(getActivity());
                            IssueJournalUser user = new IssueJournalUser();
                            user.setId("1");
                            user.setName("я");
                            issueJournal.setUser(user);
                            issueJournal.setCreatedOn(sdf.format(new Date()));
                            ((TaskCommentsFragmentAdapter) recyclerView.getAdapter()).addItem(
                                    issueJournal);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e(TAG, t.getLocalizedMessage());
                        Toast.makeText(getActivity(), R.string.error_send_new_comment, Toast.LENGTH_LONG).show();
                    }
                });
            }
            catch (NullPointerException e){
                Log.e(TAG, e.getLocalizedMessage());
                Toast.makeText(getActivity(), R.string.error_send_new_comment, Toast.LENGTH_LONG).show();
            }
        }
    }
}
