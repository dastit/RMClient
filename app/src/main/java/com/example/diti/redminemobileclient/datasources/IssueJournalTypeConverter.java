package com.example.diti.redminemobileclient.datasources;

import android.arch.persistence.room.TypeConverter;

import com.example.diti.redminemobileclient.model.IssueAssignedTo;
import com.example.diti.redminemobileclient.model.IssueJournal;
import com.example.diti.redminemobileclient.model.IssueJournalDetail;
import com.example.diti.redminemobileclient.model.IssueJournalUser;
import com.example.diti.redminemobileclient.model.Project;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class IssueJournalTypeConverter {
    public static Gson mGson = new Gson();

    @TypeConverter
    public static List<IssueJournal> fromStringToIssueJournal (String data){
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<IssueJournal>>() {}.getType();

        return mGson.fromJson(data, listType);
    }

    @TypeConverter
    public static String fromIssueJournalToString(List<IssueJournal> issueJournals){
        return mGson.toJson(issueJournals);
    }

    @TypeConverter
    public static IssueJournalUser fromStringToIssueJournalUser (String data){
        if(data == null){
            return null;
        }
        return mGson.fromJson(data, IssueJournalUser.class);
    }

    @TypeConverter
    public static String fromIssueJournalUserToString (IssueJournalUser user){
        return mGson.toJson(user);
    }

    @TypeConverter
    public static List<IssueJournalDetail> fromStringToIssueJournalDetail (String data){
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<IssueJournalDetail>>() {}.getType();

        return mGson.fromJson(data, listType);
    }

    @TypeConverter
    public static String fromIssueJournalDetailToString(List<IssueJournalDetail> details){
        return mGson.toJson(details);
    }

    @TypeConverter
    public static IssueAssignedTo fromStringToIssueAssignedTo (String data){
        if(data == null){
            return null;
        }
        return mGson.fromJson(data, IssueAssignedTo.class);
    }

    @TypeConverter
    public static String fromIssueAssignedToToString (IssueAssignedTo assignedTo){
        return mGson.toJson(assignedTo);
    }

    @TypeConverter
    public static IssueAuthor fromStringToIssueAuthor (String data){
        if(data == null){
            return null;
        }
        return mGson.fromJson(data, IssueAuthor.class);
    }

    @TypeConverter
    public static String fromIssueAuthorToString (IssueAuthor author){
        return mGson.toJson(author);
    }

    @TypeConverter
    public static IssueStatus fromStringToIssueStatus (String data){
        if(data == null){
            return null;
        }
        return mGson.fromJson(data, IssueStatus.class);
    }

    @TypeConverter
    public static String fromIssueStatusToString (IssueStatus status){
        return mGson.toJson(status);
    }

    @TypeConverter
    public static Project fromStringToProject (String data){
        if(data == null){
            return null;
        }
        return mGson.fromJson(data, Project.class);
    }

    @TypeConverter
    public static String fromProjectToString (Project project){
        return mGson.toJson(project);
    }
}
