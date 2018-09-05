package com.example.diti.redminemobileclient.datasources;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.diti.redminemobileclient.model.Issue;

@Database(entities = {Issue.class}, version = 9)
@TypeConverters(IssueJournalTypeConverter.class)
public abstract class IssueDatabase extends RoomDatabase {
    public abstract IssueDao mIssueDao();
}
