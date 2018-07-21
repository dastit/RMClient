package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class PagedTasksListViewModelFactory implements ViewModelProvider.Factory {
    PagedTaskListRepository mPagedTaskListRepository;

    public PagedTasksListViewModelFactory(PagedTaskListRepository repository) {
        mPagedTaskListRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PagedTasksListViewModel.class)) {
            return (T) new PagedTasksListViewModel(mPagedTaskListRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
