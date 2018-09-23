package com.example.diti.redminemobileclient.datasources;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class PositionalTaskDataSource extends PositionalDataSource<Issue> {

    private static final String TAG = "PositionalTaskDataSourc";
    RedmineRestApiClient.RedmineClient mRedmineClient;
    Issues result = new Issues();
    String sortBy;

    public PositionalTaskDataSource(RedmineRestApiClient.RedmineClient client,
                                    String sortByColumn) {
        sortBy = sortByColumn;
        mRedmineClient = client;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams params,
                            @NonNull final LoadInitialCallback callback) {
        Log.d(TAG, "loadInitial, requestedStartPosition = " + params.requestedStartPosition +
                ", requestedLoadSize = " + params.requestedLoadSize);

        Call<Issues> call =
                mRedmineClient.reposForTasks(params.requestedStartPosition,
                                             params.requestedLoadSize, sortBy);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                result = (Issues) response.body();
                if (params.placeholdersEnabled) {
                    callback.onResult(result.getIssues(), result.getOffset(),
                                      result.getTotalCount());
                } else {
                    callback.onResult(result.getIssues(), result.getOffset());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            List<String> errorList = new ArrayList<>();
            errorList.add(e.getLocalizedMessage());
            callback.onResult(Collections.EMPTY_LIST, 0, 0);
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
            callback.onResult(result.getIssues());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            List<String> errorList = new ArrayList<>();
            errorList.add(e.getLocalizedMessage());
            callback.onResult(Collections.EMPTY_LIST);
        }
    }
}
