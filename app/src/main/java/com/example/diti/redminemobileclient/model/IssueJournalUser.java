package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class IssueJournalUser {

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String name;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;

    public IssueJournalUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
