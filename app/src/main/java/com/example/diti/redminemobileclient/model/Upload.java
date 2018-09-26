package com.example.diti.redminemobileclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Upload {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("filename")
    @Expose
    private String filename;

    @SerializedName("content_type")
    @Expose
    private String contentType;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
