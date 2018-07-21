package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.example.diti.redminemobileclient.model.Issue;

public class PagedTasksListViewModel extends ViewModel {
    public LiveData<PagedList<Issue>> pagedList;
    PagedTaskListRepository mPagedTaskListRepository;

    public PagedTasksListViewModel(PagedTaskListRepository repository) {
        mPagedTaskListRepository = repository;
        pagedList = mPagedTaskListRepository.getPagedList();
    }
}
