package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.example.diti.redminemobileclient.datasources.IssueJournalTypeConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class IssueAttachment {

    @ColumnInfo(name = "description")
    @SerializedName("description")
    @Expose
    private String description;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;

    @TypeConverters(IssueJournalTypeConverter.class)
    @SerializedName("author")
    @Expose
    private IssueJournalUser author;

    @ColumnInfo(name = "filename")
    @SerializedName("filename")
    @Expose
    private String filename;

    @ColumnInfo(name = "content_type")
    @SerializedName("content_type")
    @Expose
    private String content_type;

    @ColumnInfo(name = "content_url")
    @SerializedName("content_url")
    @Expose
    private String content_url;

    @ColumnInfo(name = "local_path")
    private String local_path;

    //filesize in bytes
    @ColumnInfo(name = "filesize")
    @SerializedName("filesize")
    @Expose
    private String filesize;

    public IssueAttachment() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IssueJournalUser getAuthor() {
        return author;
    }

    public void setAuthor(IssueJournalUser author) {
        this.author = author;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getLocal_path() {
        return local_path;
    }

    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }
}
