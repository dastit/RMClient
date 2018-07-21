package com.example.diti.redminemobileclient.retrofit;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.model.Projects;
import com.example.diti.redminemobileclient.model.Users;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RedmineRestApiClient {

    public static RedmineClient getRedmineClient(String login, String password) {
        return ServiceGenerator.createService(RedmineClient.class, login, password);
    }

    public static RedmineClient getRedmineClient(String authToken) {
        return ServiceGenerator.createService(RedmineClient.class, authToken);
    }

    public interface RedmineClient {

        @GET("/users/current.json")
        Call<Users> reposForUser();

        @GET("/issues.json?assigned_to_id=me")
        Call<Issues> reposForTasks(@Query("offset") int offset, @Query("limit") int limit, @Query("sort") String sortByColumn);

        @GET("/projects.json")
        Call<Projects> reposForProjects(@Query("offset") int offset, @Query("limit") int limit, @Query("sort") String sortByColumn);

        @GET("/issues/{issueId}.json")
        Call<Issue> reposForTask(@Path("issueId") String issueId);
    }
}
