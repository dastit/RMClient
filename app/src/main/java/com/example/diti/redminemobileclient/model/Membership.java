package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Membership implements Parcelable {

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

    protected Membership(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        project = (Project) in.readValue(Project.class.getClassLoader());
        user = (MembershipUser) in.readValue(MembershipUser.class.getClassLoader());
    }

    @Override
    @Ignore
    public int describeContents() {
        return 0;
    }

    @Override
    @Ignore
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeValue(project);
        dest.writeValue(user);
    }

    @SuppressWarnings("unused")
    @Ignore
    public static final Parcelable.Creator<Membership> CREATOR = new Parcelable.Creator<Membership>() {
        @Override
        public Membership createFromParcel(Parcel in) {
            return new Membership(in);
        }

        @Override
        public Membership[] newArray(int size) {
            return new Membership[size];
        }
    };
}
