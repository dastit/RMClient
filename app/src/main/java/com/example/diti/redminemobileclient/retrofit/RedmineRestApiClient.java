package com.example.diti.redminemobileclient.retrofit;

import com.example.diti.redminemobileclient.datasources.IssueResponse;
import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.model.Membership;
import com.example.diti.redminemobileclient.model.Memberships;
import com.example.diti.redminemobileclient.model.Projects;
import com.example.diti.redminemobileclient.model.Users;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class RedmineRestApiClient {

    public static RedmineClient getRedmineClient(String login, String password, File cachePath) {
        return ServiceGenerator.createService(RedmineClient.class, login, password, cachePath);
    }

    public static RedmineClient getRedmineClient(String authToken, File cachePath) {
        return ServiceGenerator.createService(RedmineClient.class, authToken, cachePath);
    }

    public interface RedmineClient {

        @GET("/users/current.json")
        Call<Users> reposForUser();

        @GET("/issues.json?assigned_to_id=me")
        Call<Issues> reposForTasks(@Query("offset") int offset, @Query("limit") int limit, @Query("sort") String sortByColumn);

        @GET("/issues.json")
        Call<Issues> reposForTasksInProject(@Query("project_id") String projectId, @Query("offset") int offset, @Query("limit") int limit, @Query("sort") String sortByColumn);

        @GET("/projects.json")
        Call<Projects> reposForProjects(@Query("offset") int offset, @Query("limit") int limit, @Query("sort") String sortByColumn);

        @GET("/issues/{issueId}.json?include=attachments,journals")
        Call<IssueResponse> reposForTask(@Path("issueId") String issueId);

        @GET
        Call<ResponseBody> getAttachment (@Url String url);

        @GET("projects/{projectId}/memberships.json")
        Call<Memberships> reposForMembership(@Path("projectId") String id);
    }
}
