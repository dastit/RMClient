package com.example.diti.redminemobileclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("mail")
    @Expose
    private String mail;
    @SerializedName("last_login_on")
    @Expose
    private String lastLoginOn;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("firstname")
    @Expose
    private String firstname;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getLastLoginOn() {
        return lastLoginOn;
    }

    public void setLastLoginOn(String lastLoginOn) {
        this.lastLoginOn = lastLoginOn;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

}