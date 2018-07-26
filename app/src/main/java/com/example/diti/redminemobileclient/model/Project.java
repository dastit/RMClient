package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Project {
    @ColumnInfo(name = "description")
    @SerializedName("description")
    @Expose
    private String description;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String name;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "updated_on")
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;

    @ColumnInfo(name = "created_on")
    @SerializedName("created_on")
    @Expose
    private String createdOn;

    @ColumnInfo(name = "status")
    @SerializedName("status")
    @Expose
    private Integer status;

    @ColumnInfo(name = "identifier")
    @SerializedName("identifier")
    @Expose
    private String identifier;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
