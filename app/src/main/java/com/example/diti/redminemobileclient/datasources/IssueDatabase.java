package com.example.diti.redminemobileclient.datasources;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.diti.redminemobileclient.model.Issue;

@Database(entities = {Issue.class}, version = 1)
public abstract class IssueDatabase extends RoomDatabase {
    public abstract IssueDao mIssueDao();
}
