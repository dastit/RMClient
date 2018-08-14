package com.example.diti.redminemobileclient.datasources;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class PositionalTaskDataSource extends PositionalDataSource<Issue> {

    private static final String TAG = "PositionalTaskDataSourc";
    RedmineRestApiClient.RedmineClient mRedmineClient;
    Issues result = new Issues();
    String sortBy;

    public PositionalTaskDataSource(RedmineRestApiClient.RedmineClient client, String sortByColumn) {
        sortBy = sortByColumn;
        mRedmineClient = client;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams params, @NonNull final LoadInitialCallback callback) {
        Log.d(TAG, "loadInitial, requestedStartPosition = " + params.requestedStartPosition +
                   ", requestedLoadSize = " + params.requestedLoadSize);

        Call<Issues> call =
                mRedmineClient.reposForTasks(params.requestedStartPosition, params.requestedLoadSize, sortBy);
//        call.enqueue(new Callback<Issues>() {
//            @Override
//            public void onResponse(Call<Issues> call, IssueResponse<Issues> response) {
//                if(response.isSuccessful()){
//                    result = response.body();
//                    if (params.placeholdersEnabled) {
//                        callback.onResult(result.getIssues(), result.getOffset(), result.getTotalCount());
//                    } else {
//                        callback.onResult(result.getIssues(), result.getOffset());
//                    }
//                }
//            }
//            @Override
//            public void onFailure(Call<Issues> call, Throwable t) {
//                Log.d(TAG, t.getLocalizedMessage());
//            }
//        });
        try {
            Response response = call.execute();
            if(response.isSuccessful()){
                result = (Issues) response.body();
                if (params.placeholdersEnabled) {
                    callback.onResult(result.getIssues(), result.getOffset(), result.getTotalCount());
                } else {
                    callback.onResult(result.getIssues(), result.getOffset());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback) {
        Log.d(TAG, "loadRange, startPosition = " + params.startPosition +
                   ", loadSize = " + params.loadSize);

        Call<Issues> call =
                mRedmineClient.reposForTasks(params.startPosition, params.loadSize, sortBy);
        try {
            result = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onResult(result.getIssues());
    }
}
