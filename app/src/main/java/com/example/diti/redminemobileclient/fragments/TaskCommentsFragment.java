package com.example.diti.redminemobileclient.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.diti.redminemobileclient.DateConverter;
import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.IssueViewModel;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.IssueJournal;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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
                        recyclerView.setAdapter(new TaskCommentsFragmentAdapter(issue.getJournals(), mListener, issue));
                    }
                }
            });
        }
        return view;
    }

    public class TaskCommentsFragmentAdapter extends RecyclerView.Adapter<TaskCommentsFragmentAdapter.ViewHolder> {

        private final List<IssueJournal>                   mValues;
        private final OnListFragmentInteractionListener mListener;
        private Issue mIssue;

        public TaskCommentsFragmentAdapter(List<IssueJournal> items, OnListFragmentInteractionListener listener, Issue issue) {
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

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View      mView;
            public final TextView  mNote;
            public final TextView  mAuthor;
            public final TextView  mDate;
            public final ImageButton mExpandButton;
            public final ImageButton mCollapseButton;
            public       IssueJournal mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNote = (TextView) view.findViewById(R.id.task_comment_note);
                mAuthor = (TextView)view.findViewById(R.id.task_comment_author);
                mDate = (TextView)view.findViewById(R.id.task_comment_date);
                mExpandButton = (ImageButton)view.findViewById(R.id.task_comment_expand_button);
                mCollapseButton = (ImageButton)view.findViewById(R.id.task_comment_collapse_button);
            }

            public void bindItem (IssueJournal journal, Issue issue){
                mNote.setText(journal.getNotes());
                mAuthor.setText(journal.getUser().getName());
                mExpandButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNote.setMaxLines(1000);
                        mCollapseButton.setVisibility(View.VISIBLE);
                        mExpandButton.setVisibility(View.GONE);
                    }
                });
                mCollapseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNote.setMaxLines(3);
                        mCollapseButton.setVisibility(View.GONE);
                        mExpandButton.setVisibility(View.VISIBLE);
                    }
                });
                mDate.setText(DateConverter.getDate(journal.getCreatedOn()));

                if (issue.getAttachments().size() != 0) {
                    new AsyncTask<Issue, Void, SpannableStringBuilder>() {
                        @Override
                        protected SpannableStringBuilder doInBackground(Issue... issues) {
                            String text = journal.getNotes();
                            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                            for (int i = 0; i < issues[0].getAttachments().size(); i++) {
                                String name = issues[0].getAttachments().get(i).getFilename();
                                if (text.contains(name)) {
                                    File file;
                                    Uri uri;
                                    try {
                                        file = new File(getActivity().getCacheDir(), name);
                                        uri = FileProvider.getUriForFile(getActivity(), "be.myapplication", file);
                                    } catch (NullPointerException npe) {
                                        getActivity().finish();
                                        break;
                                    }
                                    int firstIndex = text.indexOf(name) -
                                                     1;        //единички учитывают восклицательные знаки
                                    int lastIndex = firstIndex + name.length() +
                                                    1; //по обеим сторонам от названия картинки
                                    ImageSpan imageSpan = null;
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = Picasso.get().load(uri).get();
                                        DisplayMetrics displayMetrics = new DisplayMetrics();
                                        getActivity().getWindowManager()
                                                .getDefaultDisplay()
                                                .getMetrics(displayMetrics);
                                        int width = Math.min(displayMetrics.widthPixels, bitmap.getWidth());
                                        bitmap = Bitmap.createScaledBitmap(bitmap, width, bitmap.getHeight(), true);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    imageSpan = new ImageSpan(getActivity(), bitmap);
                                    ssb.setSpan(imageSpan, firstIndex, lastIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                            }
                            return ssb;
                        }

                        @Override
                        protected void onPostExecute(SpannableStringBuilder spannableStringBuilder) {
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
}
