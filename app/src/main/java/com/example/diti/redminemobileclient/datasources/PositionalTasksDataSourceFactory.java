package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

public class PositionalTasksDataSourceFactory extends DataSource.Factory {
    RedmineRestApiClient.RedmineClient client;
    public MutableLiveData<PositionalTaskDataSource> postLiveData;
    PositionalTaskDataSource dataSource;

    public PositionalTasksDataSourceFactory(RedmineRestApiClient.RedmineClient client) {
        this.client = client;
    }

    @Override
    public DataSource create() {
        dataSource = new PositionalTaskDataSource(client, "updated_on:desc");

        postLiveData = new MutableLiveData<>();
        postLiveData.postValue(dataSource);

        return dataSource;
    }

}
