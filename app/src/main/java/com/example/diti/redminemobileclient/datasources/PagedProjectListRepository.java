package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import com.example.diti.redminemobileclient.model.Project;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.util.concurrent.Executors;

public class PagedProjectListRepository {

    RedmineRestApiClient.RedmineClient client;
    PositionalProjectDataSourceFactory       mProjectDataSourceFactory;

    public PagedProjectListRepository(RedmineRestApiClient.RedmineClient client) {
        this.client = client;
    }

    public LiveData<PagedList<Project>> getPagedList(){
        mProjectDataSourceFactory = new PositionalProjectDataSourceFactory(client);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(10)
                .build();
        return new LivePagedListBuilder(mProjectDataSourceFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();
    }


}
