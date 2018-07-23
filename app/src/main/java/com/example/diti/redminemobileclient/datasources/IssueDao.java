package com.example.diti.redminemobileclient.datasources;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.diti.redminemobileclient.model.Issue;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface IssueDao {
    @Insert(onConflict = REPLACE)
    void save(Issue issue);

    @Query("SELECT * FROM issue WHERE id = :issueId")
    Issue load(Integer issueId);


}
