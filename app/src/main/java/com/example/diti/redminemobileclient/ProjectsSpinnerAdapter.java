package com.example.diti.redminemobileclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.diti.redminemobileclient.model.Project;

import java.util.List;

public class ProjectsSpinnerAdapter extends ArrayAdapter<Project>{
    private static final String TAG = "ProjectsSpinnerAdapter";
    private Context mContext;
    private List<Project> mProjects;

    public ProjectsSpinnerAdapter(@NonNull Context context, List<Project> projects) {
        super(context,0,  projects);
        mContext = context;
        mProjects = projects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.new_task_project_spinner_view, parent, false);
        }
        TextView projectName = (TextView)convertView.findViewById(R.id.spinner_project_name);
        projectName.setText(mProjects.get(position).getName());

        TextView projectId = (TextView)convertView.findViewById(R.id.spinner_project_id);
        projectId.setText(mProjects.get(position).getId().toString());
        return convertView;
    }

    @Nullable
    @Override
    public Project getItem(int position) {
        return mProjects.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);

    }
}
