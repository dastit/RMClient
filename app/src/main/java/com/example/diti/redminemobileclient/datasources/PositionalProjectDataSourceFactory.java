package com.example.diti.redminemobileclient.datasources;

import android.arch.paging.DataSource;

import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

public class PositionalProjectDataSourceFactory extends DataSource.Factory {

    RedmineRestApiClient.RedmineClient client;

    public PositionalProjectDataSourceFactory(RedmineRestApiClient.RedmineClient client) {
        this.client = client;
    }

    @Override
    public DataSource create() {
        return new PositionalProjectDataSource(client, "name");
    }
}
