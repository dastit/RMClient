package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.diti.redminemobileclient.model.Issue;

public class IssueViewModel extends ViewModel {
    private MutableLiveData<Issue> mIssueLiveData = new MutableLiveData<>();
    private IssueRepository mIssueRepository;

    public IssueViewModel(IssueRepository issueRepository) {
        mIssueRepository = issueRepository;
    }

    public void init(Integer issueId) {
        Issue issue = mIssueRepository.getIssue(issueId);
        mIssueLiveData.postValue(issue);
    }

    public void update(Integer issueId) {
        Issue issue = mIssueRepository.requestIssue(issueId);
        mIssueLiveData.postValue(issue);
    }

    public LiveData<Issue> getIssueLiveData() {
        return mIssueLiveData;
    }
}
