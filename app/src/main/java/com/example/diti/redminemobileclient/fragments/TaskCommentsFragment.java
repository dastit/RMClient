package com.example.diti.redminemobileclient.fragments;

import android.accounts.AccountManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
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
import com.example.diti.redminemobileclient.model.IssueJournalDetail;
import com.example.diti.redminemobileclient.model.IssueJournalUser;
import com.example.diti.redminemobileclient.model.Upload;
import com.example.diti.redminemobileclient.model.UploadResponse;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    public static final  String     TAG            = "TaskCommentsFragment";
    private static final int        REQUEST_CODE   = 0;
    private static final DateFormat sdf            = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final String     ARGS_AUTHTOKEN = "auth_token";

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
        recyclerView = view.findViewById(R.id.comment_list);
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
        mNewCommentButton = view.findViewById(R.id.add_comment_fab);
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

        public void addItem(IssueJournal journal) {
            mValues.add(journal);
            notifyItemInserted(getItemCount());
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View        mView;
            public final TextView    mNote;
            public final TextView    mAuthor;
            public final TextView    mDate;
            public final TextView    mDetails;
            public final ImageButton mExpandButton;
            public final ImageButton mCollapseButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNote = view.findViewById(R.id.task_comment_note);
                mAuthor = view.findViewById(R.id.task_comment_author);
                mDate = view.findViewById(R.id.task_comment_date);
                mExpandButton = view.findViewById(R.id.task_comment_expand_button);
                mCollapseButton = view.findViewById(
                        R.id.task_comment_collapse_button);
                mDetails = view.findViewById(R.id.task_comment_details_change);
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

                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        NewCommentDialog dialog = new NewCommentDialog();
                        dialog.setTargetFragment(TaskCommentsFragment.this, REQUEST_CODE);
                        dialog.show(getFragmentManager(), "NewCommentDialog", mNote.getText()
                                                                                   .toString());
                        return true;
                    }
                });

                mDate.setText(DateConverter.getDate(journal.getCreatedOn()));

                if(journal.getDetails().size()>0) {

                    StringBuilder details = new StringBuilder();
                    for (IssueJournalDetail detail : journal.getDetails()) {
                        String detailText = "Значение " + detail.getName() + " (" + detail.getProperty() + ")" +
                                " c " + detail.getOldValue() + " на " + detail.getNewValue();
                        details.append(detailText);
                        details.append("\n");
                    }
                    mDetails.setText(details);
                    mDetails.setVisibility(View.VISIBLE);
                }

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
        if (requestCode == REQUEST_CODE) {
            String commentText = data.getStringExtra(NewCommentDialog.EXTRA_COMMENT_TEXT);
            ArrayList<Uri> attachmentList = data.getParcelableArrayListExtra(NewCommentDialog
                                                                                     .EXTRA_ATTACHMENTS_LIST);

            try {

                RedmineRestApiClient.RedmineClient client = RedmineRestApiClient.getRedmineClient
                        (mAuthToken, getActivity());
                if (attachmentList.size() != 0) {
                    List<Upload> uploadList = new ArrayList<>();
                    for (int i = 0; i < attachmentList.size(); i++) {
                        Uri uri = attachmentList.get(i);
                        try {
                            ContentResolver contentResolver = getActivity().getContentResolver();

                            //read file content to byte array
                            ParcelFileDescriptor mInputPFD = getActivity().getContentResolver()
                                                                          .openFileDescriptor(uri,
                                                                                              "r");
                            FileDescriptor fileContent = mInputPFD.getFileDescriptor();
                            InputStream initialStream = new FileInputStream(
                                    fileContent);
                            byte[] buffer = new byte[initialStream.available()];
                            initialStream.read(buffer);
                            initialStream.close();

                            //get file type
                            String contentTypeString = contentResolver.getType(uri);
                            MediaType contentType = MediaType.parse(
                                    contentTypeString);
                            RequestBody requestBody = RequestBody.create(contentType,
                                                                         buffer);
                            Call<UploadResponse> postAttachments = client.sendAttachment(
                                    requestBody);
                            int finalI = i;
                            postAttachments.enqueue(new Callback<UploadResponse>() {
                                @Override
                                public void onResponse(Call<UploadResponse> call,
                                                       Response<UploadResponse> response) {
                                    if (response.isSuccessful()) {
                                        Upload upload = response.body().getUpload();
                                        upload.setContentType(contentTypeString);
                                        String name = DocumentFile.fromSingleUri(getActivity(),
                                                                                 uri).getName();
                                        upload.setFilename(name);
                                        uploadList.add(upload);
                                        if (finalI == attachmentList.size() - 1) {
                                            sendComment(commentText, client, uploadList);
                                        }
                                    } else {
                                        Toast.makeText(getActivity(),
                                                       "Не удалось загрузить файл " + uri
                                                               .getLastPathSegment(),
                                                       Toast.LENGTH_LONG)
                                             .show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UploadResponse> call, Throwable t) {
                                    Toast.makeText(getActivity(),
                                                   "Не удалось загрузить файл " + uri.getLastPathSegment(),
                                                   Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    sendComment(commentText, client, null);
                }
            } catch (NullPointerException e) {
                Log.e(TAG, e.getLocalizedMessage());
                Toast.makeText(getActivity(), R.string.error_send_new_comment, Toast.LENGTH_LONG)
                     .show();
            }
        }
    }

    private void sendComment(String commentText, RedmineRestApiClient.RedmineClient client,
                             List<Upload> uploads) {
        Issue         issue         = new Issue();
        IssueResponse issueResponse = new IssueResponse();
        issue.setNote(commentText);
        if (uploads != null) {
            issue.setUploads(uploads);
        }
        issueResponse.setIssue(issue);

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
                Toast.makeText(getActivity(), R.string.error_send_new_comment,
                               Toast.LENGTH_LONG).show();
            }
        });
    }
}
