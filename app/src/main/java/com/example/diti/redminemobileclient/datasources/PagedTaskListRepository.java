package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.util.concurrent.Executors;

//TODO: add cache
public class PagedTaskListRepository {

    RedmineRestApiClient.RedmineClient client;
    PositionalTasksDataSourceFactory mTasksDataSourceFactory;

    public PagedTaskListRepository(RedmineRestApiClient.RedmineClient client) {
        this.client = client;
        mTasksDataSourceFactory = new PositionalTasksDataSourceFactory(client);
    }

    public LiveData<PagedList<Issue>> getPagedList(){
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(10)
                .build();
        return new LivePagedListBuilder(mTasksDataSourceFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();
    }

    public PositionalTasksDataSourceFactory getDSFactory(){
        return mTasksDataSourceFactory;
    }


}
