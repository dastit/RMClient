package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.example.diti.redminemobileclient.datasources.IssueAuthor;
import com.example.diti.redminemobileclient.datasources.IssueJournalTypeConverter;
import com.example.diti.redminemobileclient.datasources.IssueStatus;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Issue  {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer issueid;

    @ColumnInfo(name = "done_ratio")
    @SerializedName("done_ratio")
    @Expose
    private Integer doneRatio;

    @ColumnInfo(name = "spent_hours")
    @SerializedName("spent_hours")
    @Expose
    private String spent_hours;

    @ColumnInfo(name = "subject")
    @SerializedName("subject")
    @Expose
    private String subject;

    @ColumnInfo(name = "updated_on")
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;

    @ColumnInfo(name = "created_on")
    @SerializedName("created_on")
    @Expose
    private String createdOn;

    @ColumnInfo(name = "estimated_hours")
    @SerializedName("estimated_hours")
    @Expose
    private String estimatedHours;



    @ColumnInfo(name = "due_date")
    @SerializedName("due_date")
    @Expose
    private String dueDate;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    @Expose
    private String description;


    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("project")
    @Expose
    private Project project;

    @ColumnInfo(name = "start_date")
    @SerializedName("start_date")
    @Expose
    private String startDate;

    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("assigned_to")
    @Expose
    private IssueAssignedTo assigned_to;

    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("author")
    @Expose
    private IssueAuthor author;



    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("status")
    @Expose
    private IssueStatus status;

    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("journals")
    @Expose
    private List<IssueJournal> journals;


    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("attachments")
    @Expose
    private List<IssueAttachment> mAttachments;

    @ColumnInfo(name = "last_request_time_in_milliseconds")
    @Expose (serialize = false)
    private Long last_request_time_in_milliseconds;

    @Ignore
    @SerializedName("notes")
    private String note;

    @Ignore
    @SerializedName("uploads")
    private List<Upload> uploads = null;

    public Long getLast_request_time_in_milliseconds() {
        return last_request_time_in_milliseconds;
    }

    public void setLast_request_time_in_milliseconds(Long last_request_time_in_milliseconds) {
        this.last_request_time_in_milliseconds = last_request_time_in_milliseconds;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIssueid() {
        return issueid;
    }

    public void setIssueid(Integer issueid) {
        this.issueid = issueid;
    }

    public Integer getDoneRatio() {
        return doneRatio;
    }

    public void setDoneRatio(Integer doneRatio) {
        this.doneRatio = doneRatio;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }


    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(String estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public List<IssueJournal> getJournals() {
        return journals;
    }

    public void setJournals(List<IssueJournal> journals) {
        this.journals = journals;
    }

    public IssueAssignedTo getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(IssueAssignedTo assigned_to) {
        this.assigned_to = assigned_to;
    }

    public IssueAuthor getAuthor() {
        return author;
    }

    public void setAuthor(IssueAuthor author) {
        this.author = author;
    }

    public String getSpent_hours() {
        return spent_hours;
    }

    public void setSpent_hours(String spent_hours) {
        this.spent_hours = spent_hours;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public List<IssueAttachment> getAttachments() {
        return mAttachments;
    }

    public void setAttachments(List<IssueAttachment> attachments) {
        mAttachments = attachments;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Upload> getUploads() {
        return uploads;
    }

    public void setUploads(List<Upload> uploads) {
        this.uploads = uploads;
    }

    public Issue() {
    }



}
