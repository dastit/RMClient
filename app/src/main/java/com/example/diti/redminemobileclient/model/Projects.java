package com.example.diti.redminemobileclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Projects {

    @SerializedName("projects")
    @Expose
    private List<Project> projects = new ArrayList<Project>();

    @SerializedName("offset")
    @Expose
    private Integer offset;

    @SerializedName("limit")
    @Expose
    private Integer limit;

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}