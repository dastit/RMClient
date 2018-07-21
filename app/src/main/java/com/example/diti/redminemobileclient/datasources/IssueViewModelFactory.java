package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class IssueViewModelFactory implements ViewModelProvider.Factory {
    private IssueRepository mIssueRepository;

    public IssueViewModelFactory(IssueRepository issueRepository) {
        mIssueRepository = issueRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(IssueViewModel.class)) {
            return (T) new IssueViewModel(mIssueRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");    }
}
