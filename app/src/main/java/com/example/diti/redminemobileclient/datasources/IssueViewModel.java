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

    public void init(Integer issueId) {
        mIssueRepository.requestNewIssueData(issueId);
        mIssueLiveData = mIssueRepository.getStoredIssue(issueId);
    }

    public void requestNewIssueData(Integer issueId) {
        mIssueRepository.requestNewIssueData(issueId);
    }

    public void getStoredIssue(Integer issueId){
        mIssueLiveData = mIssueRepository.getStoredIssue(issueId);
    }

    public LiveData<Issue> getIssueLiveData() {
        return mIssueLiveData;
    }
}
