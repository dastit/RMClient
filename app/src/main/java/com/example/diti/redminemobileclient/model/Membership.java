package com.example.diti.redminemobileclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Membership {

    @SerializedName("id")
    @Expose
    private Integer        id;
    @SerializedName("project")
    @Expose
    private Project        project;
    @SerializedName("user")
    @Expose
    private MembershipUser user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public MembershipUser getUser() {
        return user;
    }

    public void setUser(MembershipUser user) {
        this.user = user;
    }


}
