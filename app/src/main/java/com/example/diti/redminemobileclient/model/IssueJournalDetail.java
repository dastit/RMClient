package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class IssueJournalDetail {

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String name;

    @ColumnInfo(name = "new_value")
    @SerializedName("new_value")
    @Expose
    private String newValue;

    @ColumnInfo(name = "old_value")
    @SerializedName("old_value")
    @Expose
    private String oldValue;

    @ColumnInfo(name = "property")
    @SerializedName("property")
    @Expose
    private String property;

    public IssueJournalDetail() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
