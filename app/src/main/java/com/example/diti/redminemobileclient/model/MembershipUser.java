package com.example.diti.redminemobileclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MembershipUser implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MembershipUser){
            MembershipUser user = (MembershipUser)obj;
            return this.getId() == user.getId();
        }
        return false;
    }

    protected MembershipUser(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MembershipUser> CREATOR = new Parcelable.Creator<MembershipUser>() {
        @Override
        public MembershipUser createFromParcel(Parcel in) {
            return new MembershipUser(in);
        }

        @Override
        public MembershipUser[] newArray(int size) {
            return new MembershipUser[size];
        }
    };
}
