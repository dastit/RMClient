package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.diti.redminemobileclient.model.Issue;

public class IssueViewModel extends ViewModel {
        private LiveData<Issue> mIssueLiveData;
        private IssueRepository mIssueRepository;

    public IssueViewModel(IssueRepository issueRepository) {
        mIssueRepository = issueRepository;
    }

    public void init(String issueId){
        if(mIssueLiveData != null){
            return;
        }
        mIssueLiveData = mIssueRepository.getIssue(issueId);
    }

    public LiveData<Issue> getIssueLiveData() {
        return mIssueLiveData;
    }
}
