package com.example.diti.redminemobileclient.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.datasources.IssueViewModel;
import com.example.diti.redminemobileclient.model.Issue;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TaskDetailsFragment extends Fragment {

    private static final String TAG = "TaskDetailsFragment";
    private TextView mIDAndCRDate;
    private TextView     mProjectName;
    private TextView     mAuthor;
    private TextView     mStatus;
    private TextView     mSpentHours;
    private TextView     mAssignedTo;
    private TextView     mEstimatedTime;
    private TextView     mSubject;
    private TextView     mDescription;
    private LinearLayout mLinearLayoutHeader;
    private LinearLayout mLinearLayoutExpanded1;
    private LinearLayout mLinearLayoutExpanded2;

    private OnFragmentInteractionListener mListener;
    private IssueViewModel                mIssueViewModel;

    public TaskDetailsFragment() {
    }

    public static TaskDetailsFragment newInstance() {
        TaskDetailsFragment fragment = new TaskDetailsFragment();
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

        mIDAndCRDate = (TextView) v.findViewById(R.id.task_id_and_crdate);
        mAssignedTo = (TextView) v.findViewById(R.id.task_assigned_to);
        mAuthor = (TextView) v.findViewById(R.id.task_author);
        mDescription = (TextView) v.findViewById(R.id.task_description);
        mEstimatedTime = (TextView) v.findViewById(R.id.task_estimated_time);
        mSpentHours = (TextView) v.findViewById(R.id.task_spent_hours);
        mStatus = (TextView) v.findViewById(R.id.task_status);
        mProjectName = (TextView) v.findViewById(R.id.task_project_name);
        mSubject = (TextView) v.findViewById(R.id.task_subject);
        mLinearLayoutHeader = (LinearLayout)v.findViewById(R.id.task_details_expanded_header);
        mLinearLayoutExpanded1 = (LinearLayout)v.findViewById(R.id.task_details_expanded1);
        mLinearLayoutExpanded2 = (LinearLayout)v.findViewById(R.id.task_details_expanded2);

        mIssueViewModel.getIssueLiveData().observe(getActivity(), new Observer<Issue>() {
            @Override
            public void onChanged(@Nullable Issue issue) {
                if (issue != null) {
                    setInterface(issue);
                }
            }
        });
        return v;
    }

    public void setInterface(Issue issue) {
        mLinearLayoutExpanded1.setVisibility(View.GONE);
        mLinearLayoutExpanded2.setVisibility(View.GONE);
        mLinearLayoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinearLayoutExpanded1.getVisibility()==View.GONE && mLinearLayoutExpanded2.getVisibility() == View.GONE){
                    expand();
                }
                else{
                    collapse();
                }
            }
        });

        mIDAndCRDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: try spannable to show images from description
                File file = new File(getActivity().getCacheDir(), issue.getAttachments().get(0).getFilename());

                Uri uri = FileProvider.getUriForFile(getActivity(), "be.myapplication", file);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(uri, "image/*");

                startActivity(intent);
            }
        });

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
            mIDAndCRDate.setText(
                    "Задача № " + issue.getIssueid() + " от " + day + "." + month + "." + year +
                    " " + hours + ":" + minutes);
        } catch (ParseException e) {
            mIDAndCRDate.setText("Задача № " + issue.getIssueid());
            e.printStackTrace();
        }

        mAssignedTo.setText("Назначена: " + issue.getAssigned_to().getName());
        mAuthor.setText("Автор: " + issue.getAuthor().getName());
        //mDescription.setText(issue.getDescription());
        mEstimatedTime.setText("Оценка времени: " + issue.getEstimatedHours());
        mSpentHours.setText("Потрачено: " + issue.getSpent_hours().toString());
        mStatus.setText("Статус: " + issue.getStatus().getName());
        mProjectName.setText("Проект: " + issue.getProject().getName());
        mSubject.setText(issue.getSubject());

        if(issue.getAttachments().size()!=0){
            new AsyncTask<Issue, Void, SpannableStringBuilder>() {
            @Override
                protected SpannableStringBuilder doInBackground(Issue... issues) {
                String text = issues[0].getDescription();
                SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                for (int i = 0; i<issues[0].getAttachments().size(); i++) {
                    String name = issues[0].getAttachments().get(i).getFilename();
                    if(text.contains(name)) {
                        File file = new File(getActivity().getCacheDir(), name);
                        Uri uri = FileProvider.getUriForFile(getActivity(), "be.myapplication", file);
                        int firstIndex = text.indexOf(name)-1;        //с учетом восклицательных знаков
                        int lastIndex = firstIndex + name.length()+1; //по обеим сторонам от названия
                        ImageSpan imageSpan = null;
                        Bitmap bitmap = null;
                        try {
                            bitmap = Picasso.get().load(uri).get();
                            bitmap = Bitmap.createScaledBitmap(bitmap,getView().getWidth(),bitmap.getHeight(),true);
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
                    mDescription.setText(spannableStringBuilder);
                }
            }.execute(issue);
        }
    }

    private void expand(){
        mLinearLayoutExpanded1.setVisibility(View.VISIBLE);
        mLinearLayoutExpanded2.setVisibility(View.VISIBLE);
    }

    private void collapse(){
        mLinearLayoutExpanded1.setVisibility(View.GONE);
        mLinearLayoutExpanded2.setVisibility(View.GONE);
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
