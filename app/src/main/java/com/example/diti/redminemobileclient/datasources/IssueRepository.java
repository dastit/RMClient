package com.example.diti.redminemobileclient.datasources;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.IssueAttachment;
import com.example.diti.redminemobileclient.retrofit.RedmineRestApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueRepository {
    private static final String TAG = "IssueRepository";
    private final RedmineRestApiClient.RedmineClient mRedmineClient;
    private final IssueDao                           mIssueDao;
    public        Context                            mContext;

    public IssueRepository(RedmineRestApiClient.RedmineClient mRedmineClient, IssueDao mIssueDao,
                           Context context) {
        this.mRedmineClient = mRedmineClient;
        this.mIssueDao = mIssueDao;
        mContext = context;
    }

    public LiveData<Issue> getStoredIssue(Integer issueId) {
        return mIssueDao.load(issueId);
    }

    public void requestNewIssueData(Integer issueId) {
        Call<IssueResponse> call = mRedmineClient.reposForTaskFull(issueId.toString());
        call.enqueue(new Callback<IssueResponse>() {
            @Override
            public void onResponse(Call<IssueResponse> call, Response<IssueResponse> response) {
                IssueResponse issueResponse = response.body();
                Issue         issue         = issueResponse.getIssue();
                issue.setLast_request_time_in_milliseconds(System.currentTimeMillis());
                mIssueDao.save(issue);

                if (!issue.getAttachments().isEmpty()) {
                    for (IssueAttachment attach : issue.getAttachments()) {
                        String             url            = attach.getContent_url();
                        Call<ResponseBody> callForPicture = mRedmineClient.getAttachment(url);
                        callForPicture.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call,
                                                   Response<ResponseBody> response) {
                                ResponseBody body     = response.body();
                                String       pathname = null;
                                if (body.contentLength() != 0) {
                                    String fileName = Uri.parse(url).getLastPathSegment();
                                    try {
                                        pathname = mContext.getCacheDir() + File.separator
                                                + fileName;
                                        File futureStudioIconFile = new File(pathname);

                                        InputStream  inputStream  = null;
                                        OutputStream outputStream = null;

                                        try {
                                            byte[] fileReader = new byte[4096];

                                            long fileSize           = body.contentLength();
                                            long fileSizeDownloaded = 0;

                                            inputStream = body.byteStream();
                                            outputStream = new FileOutputStream(
                                                    futureStudioIconFile);

                                            while (true) {
                                                int read = inputStream.read(fileReader);

                                                if (read == -1) {
                                                    break;
                                                }

                                                outputStream.write(fileReader, 0, read);

                                                fileSizeDownloaded += read;

                                                Log.d(TAG,
                                                      "file download: " + fileSizeDownloaded + " of " +
                                                              fileSize);
                                            }

                                            outputStream.flush();
                                        } catch (IOException e) {
                                        } finally {
                                            if (inputStream != null) {
                                                inputStream.close();
                                            }

                                            if (outputStream != null) {
                                                outputStream.close();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (pathname != null) {
                                    attach.setLocal_path(pathname);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<IssueResponse> call, Throwable t) {

            }
        });
    }

    public boolean isExpired(Issue issue) {
        if (issue == null || issue.getLast_request_time_in_milliseconds() == 0 ||
                issue.getLast_request_time_in_milliseconds() <
                        System.currentTimeMillis() - (1000)) {
            return true;
        }
        int flag = 0;
        for (int i = 0; i < issue.getAttachments().size(); i++) {
            for (String name : mContext.getCacheDir().list()) {
                String attachmentName = issue.getAttachments().get(i).getFilename();
                if (attachmentName.equals(name)) {
                    flag++;
                    break;
                }
            }
        }
        if (flag < issue.getAttachments().size()) {
            return true;
        }
        return false;
    }
}
