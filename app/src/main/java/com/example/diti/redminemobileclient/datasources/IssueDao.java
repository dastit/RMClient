package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.diti.redminemobileclient.model.Issue;

import java.util.Date;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface IssueDao {
    @Insert(onConflict = REPLACE)
    void save(Issue issue);

    @Query("SELECT * FROM issue WHERE id = :issueId")
    LiveData<Issue> load(String issueId);

    @Query("SELECT COUNT(id) FROM issue WHERE id = :issueId AND last_update >= :last_update")
    Integer hasIssue(String issueId, long last_update);
}
