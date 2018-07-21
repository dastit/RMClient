package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.example.diti.redminemobileclient.model.Project;

public class PagedProjectListViewModel extends ViewModel {
    public LiveData<PagedList<Project>> pagedList;
    PagedProjectListRepository mPagedProjectListRepository;

    public PagedProjectListViewModel(PagedProjectListRepository repository) {
        mPagedProjectListRepository = repository;
        pagedList = mPagedProjectListRepository.getPagedList();
    }
}
