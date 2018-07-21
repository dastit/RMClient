package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;


public class PagedProjectListViewModelFactory implements ViewModelProvider.Factory {
    PagedProjectListRepository mPagedProjectListRepository;

    public PagedProjectListViewModelFactory(PagedProjectListRepository repository) {
        mPagedProjectListRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PagedProjectListViewModel.class)) {
            return (T) new PagedProjectListViewModel(mPagedProjectListRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
