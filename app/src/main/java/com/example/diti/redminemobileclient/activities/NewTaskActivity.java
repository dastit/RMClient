package com.example.diti.redminemobileclient.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.diti.redminemobileclient.GetMembershipUsersAsyncTask;
import com.example.diti.redminemobileclient.MyVerticalStepperFormLayout;
import com.example.diti.redminemobileclient.ParentIssueAsyncTask;
import com.example.diti.redminemobileclient.ProjectListAsyncTask;
import com.example.diti.redminemobileclient.R;
import com.example.diti.redminemobileclient.fragments.AssignedToListDialog;
import com.example.diti.redminemobileclient.fragments.ProjectListDialog;
import com.example.diti.redminemobileclient.fragments.SupervisorListDialog;
import com.example.diti.redminemobileclient.model.Issue;
import com.example.diti.redminemobileclient.model.Membership;
import com.example.diti.redminemobileclient.model.Project;

import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class NewTaskActivity extends AppCompatActivity implements VerticalStepperForm, ProjectListDialog.ProjectListDialogListener, ProjectListAsyncTask.TaskDelegate, GetMembershipUsersAsyncTask.TaskDelegate, AssignedToListDialog.AssignedToListDialogListener, ParentIssueAsyncTask.TaskDelegate, SupervisorListDialog.SupervisorListDialogListener {
    public final static String EXTRA_AUTH = "auth_token";
    private static final int REQUEST_IMAGE_GET = 1;
    private static final String TAG = "NewTaskActivity";
    private static final String STATE_PROJECT_ID = "projectId";
    private static final String STATE_ASSIGNED_TO_ID = "assignedToId";
    private static final String STATE_PARENT_ISSUE_ID = "parentIssueId";
    private static final String STATE_ATTACHMENTS = "attachments";
    private static final String STATE_SUPERVISORS = "supervisors";

    private MyVerticalStepperFormLayout verticalStepperForm;
    private Button mProjectButton;
    private TextView mChosenProject;
    private TextView mChosenProjectId;

    private EditText mSubject;
    private EditText mDescription;
    private Spinner mPriority;
    private Button mAssignedToButton;
    private TextView mAssignedTo;
    private TextView mAssignedToId;
    private AutoCompleteTextView mParentIssue;
    private EditText mEstimatedTime;
    private Button mAttachmentsButton;
    private TextView mAttachmentsList;
    private Button mSupervisorsButton;
    private TextView mSupervisors;
    private ProgressBar mProgressBar;
    private TextView mConfirmationContent;

    private String mAuthToken;

    private String chosenProjectID;
    private String chosenProjectName;
    private String chosenAssignedToID;
    private String chosenAssignedTo;
    private String savedSubject;
    private String savedDescription;
    private String chosenPriority;
    private String chosenParentIssueId;
    private String chosenParentIssue;
    private String savedEstimatedTime;
    private ArrayList<Uri> attachmentList = new ArrayList<>();
    private ArrayMap<Integer, String> supervisorIds = new ArrayMap<>();
    private String supervisorsListString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_1);

        mProgressBar = (ProgressBar) findViewById(R.id.new_task_progress_bar);
        mAuthToken = getIntent().getStringExtra(EXTRA_AUTH);

        CharSequence[] defaultPriority = getResources().getTextArray(R.array.issue_priorities);
        chosenPriority = (String) defaultPriority[0];
        supervisorsListString = "Нет";

        String[] mySteps = getResources().getStringArray(R.array.new_task_steps);

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

        // Finding the view
        verticalStepperForm = (MyVerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);

        // Setting up and initializing the form
        MyVerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)// It is true by default, so in this case this line is not necessary
                .init();
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createProjectStep();
                break;
            case 1:
                view = createSubjectStep();
                break;
            case 2:
                view = createDescriptionStep();
                break;
            case 3:
                view = createPriorityStep();
                break;
            case 4:
                view = createAssignedToStep();
                break;
            case 5:
                view = createParentIssueStep();
                break;
            case 6:
                view = createEstimatedTimeStep();
                break;
            case 7:
                view = createAttachmentsStep();
                break;
            case 8:
                view = createSupervisorsStep();
                break;
            case 100:
                view = createConfirmationStep();
        }
        return view;
    }

    private View createProjectStep() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout projects = (LinearLayout) inflater.inflate(R.layout.new_task_projects, null, false);
        mProjectButton = (Button) projects.findViewById(R.id.new_task_project_button);
        mProjectButton.setText(getString(R.string.new_task_project_label));
        mProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                ProjectListAsyncTask task = new ProjectListAsyncTask(NewTaskActivity.this, mAuthToken, NewTaskActivity.this);
                task.execute();
            }
        });
        mChosenProject = (TextView) projects.findViewById(R.id.new_task_chosen_project);
        mChosenProject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkProjectStep();
            }
        });
        mChosenProject.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        mChosenProjectId = (TextView) projects.findViewById(R.id.new_task_chosen_project_id);

        return projects;
    }

    private View createSubjectStep() {
        mSubject = new EditText(this);
        mSubject.setHint(getString(R.string.new_task_subject_label));
        mSubject.setSingleLine();
        mSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSubjectStep();
            }
        });
        mSubject.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return mSubject;
    }

    private View createDescriptionStep() {
        LayoutInflater inflater = LayoutInflater.from(this);
        ScrollView descriptionContaner = (ScrollView) inflater.inflate(R.layout.new_task_description, null, false);
        mDescription = (EditText) descriptionContaner.findViewById(R.id.new_task_description);

        mDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkDescriptionStep();
            }
        });
        mDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!event.equals(EditorInfo.IME_NULL)) {
                    return false;
                }
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return descriptionContaner;
    }

    private View createPriorityStep() {
        mPriority = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.issue_priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPriority.setAdapter(adapter);
        mPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenPriority = (String) parent.getItemAtPosition(position);
                verticalStepperForm.setActiveStepAsCompleted();
                verticalStepperForm.goToNextStep();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                verticalStepperForm.setActiveStepAsUncompleted(null);
            }
        });
        return mPriority;
    }

    private View createAssignedToStep() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout assignedToLayout = (LinearLayout) inflater.inflate(R.layout.new_task_assigned_to, null, false);
        mAssignedToButton = (Button) assignedToLayout.findViewById(R.id.new_task_assigned_to_button);
        mAssignedToButton.setText(getString(R.string.new_task_assigned_to_label));
        mAssignedToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                DialogFragment dialog = new AssignedToListDialog();
                GetMembershipUsersAsyncTask task = new GetMembershipUsersAsyncTask(NewTaskActivity.this, mAuthToken, chosenProjectID, dialog, null);
                task.execute();
            }
        });
        mAssignedTo = (TextView) assignedToLayout.findViewById(R.id.new_task_assigned_to);
        mAssignedTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkAssignedToStep();
            }
        });
        mAssignedTo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        mAssignedToId = (TextView) assignedToLayout.findViewById(R.id.new_task_assigned_to_id);
        return assignedToLayout;
    }

    private View createEstimatedTimeStep() {
        mEstimatedTime = new EditText(this);
        mEstimatedTime.setSingleLine(true);
        mEstimatedTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEstimatedTimeStep();
            }
        });
        mEstimatedTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return mEstimatedTime;
    }

    private View createParentIssueStep() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout parentIssue =
                (LinearLayout) inflater.inflate(R.layout.new_task_parent_issue, null, false);
        mParentIssue = (AutoCompleteTextView) parentIssue.findViewById(R.id.new_task_parent_issue);
        mParentIssue.setHint(getString(R.string.new_task_parent_issue_label));
        mParentIssue.setThreshold(3);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mParentIssue.setDropDownWidth(displayMetrics.widthPixels);

        mParentIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                verticalStepperForm.setActiveStepAsCompleted();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mParentIssue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkParentIssueStep();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mParentIssue.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    String insertedValue = mParentIssue.getText().toString();
                    if (!insertedValue.isEmpty()) {
                        ListAdapter adapter = mParentIssue.getAdapter();
                        for (int i = 0; i < adapter.getCount(); i++) {
                            if (adapter.getItem(i).equals(insertedValue)) {
                                chosenParentIssue = insertedValue;
                                chosenParentIssueId = insertedValue.substring(0, 4);
                                return;

                            }
                        }
                        verticalStepperForm.setActiveStepAsUncompleted(getString(R.string.new_task_parent_issue_is_wrong));
                    }
                }
            });
        }
        mParentIssue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return parentIssue;
    }

    private View createAttachmentsStep() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout attachments = (LinearLayout) inflater.inflate(R.layout.new_task_attachments, null, false);
        mAttachmentsButton = (Button) attachments.findViewById(R.id.new_task_attachments_button);
        mAttachmentsButton.setText(getString(R.string.new_task_attachments_button));
        mAttachmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });
        mAttachmentsList = (TextView) attachments.findViewById(R.id.new_task_attachments_list);
        mAttachmentsList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkAttachmentsStep();
            }
        });
        mAttachmentsList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return attachments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            String list = "";
            Uri uri;
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
                String name = DocumentFile.fromSingleUri(this, u).getName();
                list = list + name + "\n";
            }
            mAttachmentsList.setText(list);
        }
    }

    private View createSupervisorsStep() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout supervisors = (LinearLayout) inflater.inflate(R.layout.new_task_supervisors, null, false);
        mSupervisorsButton = (Button) supervisors.findViewById(R.id.new_task_supervisors_button);
        mSupervisorsButton.setText(getString(R.string.new_task_supervisors_label));
        mSupervisorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                DialogFragment dialog = new SupervisorListDialog();
                GetMembershipUsersAsyncTask task = new GetMembershipUsersAsyncTask(NewTaskActivity.this, mAuthToken, chosenProjectID, dialog, supervisorIds);
                task.execute();
            }
        });
        mSupervisors = (TextView) supervisors.findViewById(R.id.new_task_supervisors);
        mSupervisors.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSupervisors.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return supervisors;
    }

    private View createConfirmationStep() {
        mConfirmationContent = new TextView(this);
        return mConfirmationContent;
    }


    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                checkParentIssueStep();
                break;
            case 6:
                break;
            case 7:
                checkAttachmentsStep();
                break;
            case 8:
                checkSupervisorsStep();
                break;
            case 9:
                generateConfirmationText();
                break;

        }
    }

    private void checkProjectStep() {
        if (mChosenProject.getText().length() > 0) {
            ParentIssueAsyncTask task = new ParentIssueAsyncTask(NewTaskActivity.this, mAuthToken, chosenProjectID);
            task.execute();
            verticalStepperForm.setActiveStepAsCompleted();
            verticalStepperForm.goToNextStep();
        } else {
            String errorMessage = getString(R.string.new_task_empty_project_error);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }

    }

    private void checkSubjectStep() {
        if (mSubject.getText().length() > 0) {
            savedSubject = mSubject.getText().toString();
            verticalStepperForm.setActiveStepAsCompleted();

        } else {
            String errorMessage = getString(R.string.new_task_empty_subject_error);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkDescriptionStep() {
        if (mDescription.getText().length() > 0) {
            savedDescription = mDescription.getText().toString();
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            String errorMessage = getString(R.string.new_task_empty_description_error);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkAssignedToStep() {
        if (mAssignedTo.getText().length() > 0) {
            verticalStepperForm.setActiveStepAsCompleted();
            //verticalStepperForm.goToNextStep();
        } else {
            String errorMessage = getString(R.string.new_task_empty_assigned_to_error);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkParentIssueStep() {
        String content = mParentIssue.getText().toString();
        if (content.isEmpty() || content.length()>4) {
            verticalStepperForm.setActiveStepAsCompleted();
            return;
        }else{
            verticalStepperForm.setActiveStepAsUncompleted(getString(R.string.new_task_parent_issue_is_wrong));
        }
    }

    private void checkEstimatedTimeStep() {
        if (mEstimatedTime.getText().length() > 0) {
            savedEstimatedTime = mEstimatedTime.getText().toString();
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            String errorMessage = getString(R.string.new_task_empty_estimated_time_error);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkAttachmentsStep() {
        verticalStepperForm.setActiveStepAsCompleted();
    }

    private void checkSupervisorsStep() {

        verticalStepperForm.setActiveStepAsCompleted();
    }

    private void generateConfirmationText() {
        mConfirmationContent.setText("");
        String attachmentsListString = "Нет";
        if (!attachmentList.isEmpty()) {
            attachmentsListString = "\n";
            for (Uri u : attachmentList) {
                String name = DocumentFile.fromSingleUri(this, u).getName();
                attachmentsListString = attachmentsListString + name + "\n";
            }
        }


        String[] stepNames = getResources().getStringArray(R.array.new_task_steps);
        SpannableString[] ss = new SpannableString[stepNames.length];
        int i = 0;
        for (String name : stepNames) {
            ss[i] = new SpannableString(name + ": ");
            ss[i].setSpan(new StyleSpan(Typeface.BOLD), 0, ss[i].length(), 0);
            i++;
        }
        mConfirmationContent.append(ss[0]);
        mConfirmationContent.append(chosenProjectName + "\n");

        mConfirmationContent.append(ss[1]);
        mConfirmationContent.append(savedSubject + "\n");

        mConfirmationContent.append(ss[2]);
        mConfirmationContent.append(savedDescription + "\n");

        mConfirmationContent.append(ss[3]);
        mConfirmationContent.append(chosenPriority + "\n");

        mConfirmationContent.append(ss[4]);
        mConfirmationContent.append(chosenAssignedTo + "\n");

        mConfirmationContent.append(ss[5]);
        mConfirmationContent.append(chosenParentIssue + "\n");

        mConfirmationContent.append(ss[6]);
        mConfirmationContent.append(savedEstimatedTime + "\n");

        mConfirmationContent.append(ss[7]);
        mConfirmationContent.append(attachmentsListString + "\n");

        mConfirmationContent.append(ss[8]);
        mConfirmationContent.append(supervisorsListString + "\n");
    }


    @Override
    public void sendData() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PROJECT_ID, chosenProjectID);
        //outState.putString(STATE_PROJECT_NAME, chosenProjectName);
        //outState.putString(STATE_SUBJECT, savedSubject);
        //outState.putString(STATE_DESCRIPTION, savedDescription);
        outState.putString(STATE_ASSIGNED_TO_ID, chosenAssignedToID);
        //outState.putString(STATE_ASSIGNED_TO, chosenAssignedTo);
        //outState.putString(STATE_PRIORITY, chosenPriority);
        //outState.putString(STATE_ESTIMATED_TIME, savedEstimatedTime);
        outState.putString(STATE_PARENT_ISSUE_ID, chosenParentIssueId);
        //outState.putString(STATE_PARENT_ISSUE, chosenParentIssue);
        outState.putParcelableArrayList(STATE_ATTACHMENTS, attachmentList);
        ArrayList<Integer> supervisorIdList = new ArrayList<>(supervisorIds.keySet());
        outState.putIntegerArrayList(STATE_SUPERVISORS, supervisorIdList);
    }

    @Override
    public void removeProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    //for typed parent issue
    @Override
    public void createAdaptedForParentIssues(List<Issue> issues) {
        String[] issueNames = new String[issues.size()];
        for (int i = 0; i < issueNames.length; i++) {
            issueNames[i] = String.valueOf(issues.get(i).getIssueid()) + ": " + issues.get(i).getSubject();
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, issueNames);
        mParentIssue.setAdapter(adapter);
        Log.d(TAG, String.valueOf(adapter.getCount()));
    }

    //for project
    @Override
    public void onItemClick(Project project) {
        mChosenProject.setText(project.getName());
        chosenProjectID = String.valueOf(project.getId());
        chosenProjectName = project.getName();
    }

    //for assigned to
    @Override
    public void onItemClick(Membership membership) {
        mAssignedTo.setText(membership.getUser().getName());
        mAssignedToId.setText(String.valueOf(membership.getUser().getId()));
        chosenAssignedToID = String.valueOf(membership.getUser().getId());
        chosenAssignedTo = membership.getUser().getName();
    }


    //for supervisors list
    @Override
    public void onItemClick(ArrayMap<Integer, String> users) {
        String list = "";
        if (!users.isEmpty()) {
            for (String user : users.values()) {
                list += user + "\n";
            }
            mSupervisors.setText(list);

            supervisorsListString = "\n" + list;
        }

    }
}
