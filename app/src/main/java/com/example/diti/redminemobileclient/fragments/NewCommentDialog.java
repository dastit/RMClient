package com.example.diti.redminemobileclient.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.provider.DocumentFile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diti.redminemobileclient.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class NewCommentDialog extends DialogFragment {
    public static final  String EXTRA_COMMENT_TEXT     = "comment_text";
    private static final int    REQUEST_RESULT_CODE    = 1;
    private static final int    REQUEST_IMAGE_GET      = 2;
    public static final String  EXTRA_ATTACHMENTS_LIST = "attachments_list";
    public EditText    comment;
    public ImageButton attachFileButton;
    public String      quoteText;
    public ArrayList<Uri>   attachmentList;
    public String      list;
    public TextView    mAttachmentsList;


    public NewCommentDialog() {
        // Required empty public constructor
    }

    public void show(FragmentManager manager, String tag, String quote) {
        quoteText = quote;
        super.show(manager, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        attachmentList = new ArrayList<>();
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        LayoutInflater      inflater = LayoutInflater.from(getActivity());
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.new_comment, null, false);
        mAttachmentsList = layout.findViewById(R.id.new_comment_attachment_list);

        comment = layout.findViewById(R.id.new_comment_text);
        comment.setMinLines(10);
        comment.setRawInputType(EditorInfo.TYPE_CLASS_TEXT);
        if (quoteText != null) {
            comment.setText(String.format(">%s\n", quoteText), TextView.BufferType.EDITABLE);
            comment.setSelection(comment.getText().length());
        }

        attachFileButton = layout.findViewById(R.id.new_comment_attach_file_button);
        attachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });
        return builder.setView(layout)
                      .setPositiveButton(android.R.string.ok,
                                         new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialogInterface,
                                                                 int i) {
                                                 String commentText = comment.getText()
                                                                             .toString();
                                                 Intent intent = new Intent();
                                                 intent.putExtra(EXTRA_COMMENT_TEXT,
                                                                 commentText);
                                                 intent.putParcelableArrayListExtra
                                                         (EXTRA_ATTACHMENTS_LIST, attachmentList);
                                                 getTargetFragment().onActivityResult
                                                         (getTargetRequestCode(),
                                                          REQUEST_RESULT_CODE, intent);
                                             }
                                         })
                      .setNegativeButton(R.string.dont_stop_task,
                                         null)
                      .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri uri;
            list = "";

            try {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    uri = data.getClipData().getItemAt(i).getUri();
                    attachmentList.add(uri);
                }
            } catch (NullPointerException npe) {
                uri = data.getData();
                attachmentList.add(uri);
            }
            for (Uri u : attachmentList) {
                String name = DocumentFile.fromSingleUri(getActivity(), u).getName();
                list = list + name + "\n";
            }
            mAttachmentsList.setVisibility(View.VISIBLE);
            mAttachmentsList.setText(list);
        }
    }

}
