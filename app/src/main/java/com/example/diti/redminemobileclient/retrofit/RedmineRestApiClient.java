package com.example.diti.redminemobileclient.retrofit;

import android.content.Context;

import com.example.diti.redminemobileclient.datasources.IssueResponse;
import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.model.Memberships;
import com.example.diti.redminemobileclient.model.Projects;
import com.example.diti.redminemobileclient.model.UploadResponse;
import com.example.diti.redminemobileclient.model.Users;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class RedmineRestApiClient {

    public static RedmineClient getRedmineClient(String login, String password, Context context)
            throws NullPointerException{
        return ServiceGenerator.createService(RedmineClient.class, login, password, context);
    }

    public static RedmineClient getRedmineClient(String authToken, Context context) throws NullPointerException{
        return ServiceGenerator.createService(RedmineClient.class, authToken, context);
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

        @GET("/issues/{issueId}.json")
        Call<IssueResponse> reposForTask(@Path("issueId") String issueId);

        @GET("/issues/{issueId}.json?include=attachments,journals")
        Call<IssueResponse> reposForTaskFull(@Path("issueId") String issueId);

        @GET
        Call<ResponseBody> getAttachment (@Url String url);

        @GET("projects/{projectId}/memberships.json")
        Call<Memberships> reposForMembership(@Path("projectId") String id);

        @PUT ("/issues/{issueId}.json")
        Call<ResponseBody> sendNewComment(@Path("issueId") String issueId, @Body IssueResponse issue);

        @Headers("Content-Type: application/octet-stream")
        @POST ("/uploads.json")
        Call<UploadResponse> sendAttachment(@Body RequestBody file);
    }
}
