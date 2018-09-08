package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.diti.redminemobileclient.model.Issue;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface IssueDao {
    @Insert(onConflict = REPLACE)
    void save(Issue issue);

    @Query("SELECT * FROM issue WHERE issueid = :issueId")
    LiveData<Issue> load(Integer issueId);

    @Update
    int update(Issue issue);
}
