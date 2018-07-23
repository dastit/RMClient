package com.example.diti.redminemobileclient.datasources;

import com.example.diti.redminemobileclient.model.Issue;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssueResponse {

    @SerializedName("issue")
    @Expose
    private Issue issue;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }
}
