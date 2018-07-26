package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.example.diti.redminemobileclient.datasources.IssueJournalTypeConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class IssueJournal {

    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("user")
    @Expose
    private IssueJournalUser user;

    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("details")
    @Expose
    private List<IssueJournalDetail> details = new ArrayList<IssueJournalDetail>();

    @ColumnInfo(name = "notes")
    @SerializedName("notes")
    @Expose
    private String notes;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "created_on")
    @SerializedName("created_on")
    @Expose
    private String createdOn;

    public IssueJournal() {
    }

    public IssueJournalUser getUser() {
        return user;
    }

    public void setUser(IssueJournalUser user) {
        this.user = user;
    }

    public List<IssueJournalDetail> getDetails() {
        return details;
    }

    public void setDetails(List<IssueJournalDetail> details) {
        this.details = details;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
