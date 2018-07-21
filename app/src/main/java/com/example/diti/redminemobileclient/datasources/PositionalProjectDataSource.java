package com.example.diti.redminemobileclient.datasources;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.diti.redminemobileclient.model.Issues;
import com.example.diti.redminemobileclient.model.Projects;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PositionalProjectDataSource extends PositionalDataSource {
    private static final String TAG = "PositionalProjectDS";
    RedmineRestApiClient.RedmineClient mRedmineClient;
    Projects result = new Projects();
    String sortBy;

    public PositionalProjectDataSource(RedmineRestApiClient.RedmineClient client, String sortByColumn) {
        sortBy = sortByColumn;
        mRedmineClient = client;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams params, @NonNull final LoadInitialCallback callback) {
        Log.d(TAG, "loadInitial, requestedStartPosition = " + params.requestedStartPosition +
                   ", requestedLoadSize = " + params.requestedLoadSize);

        Call<Projects> call =
                mRedmineClient.reposForProjects(params.requestedStartPosition, params.requestedLoadSize, sortBy);
        call.enqueue(new Callback<Projects>() {
            @Override
            public void onResponse(Call<Projects> call, Response<Projects> response) {
                if(response.isSuccessful()){
                    result = response.body();
                    if (params.placeholdersEnabled) {
                        callback.onResult(result.getProjects(), result.getOffset(), result.getTotalCount());
                    } else {
                        callback.onResult(result.getProjects(), result.getOffset());
                    }
                }
            }
            @Override
            public void onFailure(Call<Projects> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback) {
        Log.d(TAG, "loadRange, startPosition = " + params.startPosition +
                   ", loadSize = " + params.loadSize);

        Call<Projects> call =
                mRedmineClient.reposForProjects(params.startPosition, params.loadSize, sortBy);
        try {
            result = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onResult(result.getProjects());
    }
}
