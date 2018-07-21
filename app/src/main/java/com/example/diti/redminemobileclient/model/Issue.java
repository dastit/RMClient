package com.example.diti.redminemobileclient.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Issue implements Parcelable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "done_ratio")
    @SerializedName("done_ratio")
    @Expose
    private Integer doneRatio;

    @ColumnInfo(name = "subject")
    @SerializedName("subject")
    @Expose
    private String subject;

    @ColumnInfo(name = "updated_on")
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;

//   // @ColumnInfo(name = "author")
//    @SerializedName("author")
//    @Expose
//    private Author author;
//
//   // @ColumnInfo(name = "status")
//    @SerializedName("status")
//    @Expose
//    private Status status;
//
//   // @ColumnInfo(name = "priority")
//    @SerializedName("priority")
//    @Expose
//    private Priority priority;

    @ColumnInfo(name = "created_on")
    @SerializedName("created_on")
    @Expose
    private String createdOn;

   // @ColumnInfo(name = "estimated_hours")
    @SerializedName("estimated_hours")
    @Expose
    private String estimatedHours;

//    //@ColumnInfo(name = "parent")
//    @SerializedName("parent")
//    @Expose
//    private Parent parent;

    //@ColumnInfo(name = "due_date")
    @SerializedName("due_date")
    @Expose
    private String dueDate;

   // @ColumnInfo(name = "description")
    @SerializedName("description")
    @Expose
    private String description;

//    //@ColumnInfo(name = "tracker")
//    @SerializedName("tracker")
//    @Expose
//    private Tracker tracker;

    @Ignore
    @SerializedName("project")
    @Expose
    private Project project;

    @ColumnInfo(name = "start_date")
    @SerializedName("start_date")
    @Expose
    private String startDate;

    @ColumnInfo(name = "last_update")
    private long lastUpdate;
    public long getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


//    //@ColumnInfo(name = "assigned_to")
//    @SerializedName("assigned_to")
//    @Expose
//    private AssignedTo assignedTo;
//
//    //@Ignore
//    @SerializedName("custom_fields")
//    @Expose
//    private List<CustomField> customFields = new ArrayList<CustomField>();

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoneRatio() {
        return doneRatio;
    }

    public void setDoneRatio(Integer doneRatio) {
        this.doneRatio = doneRatio;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

//    public Author getAuthor() {
//        return author;
//    }
//
//    public void setAuthor(Author author) {
//        this.author = author;
//    }
//
//    public Status getStatus() {
//        return status;
//    }
//
//    public void setStatus(Status status) {
//        this.status = status;
//    }
//
//    public Priority getPriority() {
//        return priority;
//    }
//
//    public void setPriority(Priority priority) {
//        this.priority = priority;
//    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(String estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

//    public Parent getParent() {
//        return parent;
//    }
//
//    public void setParent(Parent parent) {
//        this.parent = parent;
//    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

//    public Tracker getTracker() {
//        return tracker;
//    }
//
//    public void setTracker(Tracker tracker) {
//        this.tracker = tracker;
//    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

//    public AssignedTo getAssignedTo() {
//        return assignedTo;
//    }
//
//    public void setAssignedTo(AssignedTo assignedTo) {
//        this.assignedTo = assignedTo;
//    }
//
//    public List<CustomField> getCustomFields() {
//        return customFields;
//    }
//
//    public void setCustomFields(List<CustomField> customFields) {
//        this.customFields = customFields;
//    }

    public Issue() {
    }

    @Ignore
    protected Issue(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        doneRatio = in.readByte() == 0x00 ? null : in.readInt();
        subject = in.readString();
        updatedOn = in.readString();
//        author = (Author) in.readValue(Author.class.getClassLoader());
//        status = (Status) in.readValue(Status.class.getClassLoader());
 //       priority = (Priority) in.readValue(Priority.class.getClassLoader());
        createdOn = in.readString();
        estimatedHours = in.readString();
//        parent = (Parent) in.readValue(Parent.class.getClassLoader());
        dueDate = in.readString();
        description = in.readString();
 //       tracker = (Tracker) in.readValue(Tracker.class.getClassLoader());
 //       project = (Project) in.readValue(Project.class.getClassLoader());
        startDate = in.readString();
//        assignedTo = (AssignedTo) in.readValue(AssignedTo.class.getClassLoader());
//        if (in.readByte() == 0x01) {
//            customFields = new ArrayList<CustomField>();
//            in.readList(customFields, CustomField.class.getClassLoader());
//        } else {
//            customFields = null;
//        }
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        if (doneRatio == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(doneRatio);
        }
        dest.writeString(subject);
        dest.writeString(updatedOn);
        //dest.writeValue(author);
        //dest.writeValue(status);
        //dest.writeValue(priority);
        dest.writeString(createdOn);
        dest.writeString(estimatedHours);
        //dest.writeValue(parent);
        dest.writeString(dueDate);
        dest.writeString(description);
        //dest.writeValue(tracker);
        //dest.writeValue(project);
        dest.writeString(startDate);
        //dest.writeValue(assignedTo);
//        if (customFields == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(customFields);
//        }
    }

    @Ignore
    public static final Parcelable.Creator<Issue> CREATOR = new Parcelable.Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

//
//    public class Author {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//        @SerializedName("name")
//        @Expose
//        private String  name;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }
//    public class AssignedTo {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//        @SerializedName("name")
//        @Expose
//        private String  name;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }
//    public class Status {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//        @SerializedName("name")
//        @Expose
//        private String name;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//    }
//    public class Tracker {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//        @SerializedName("name")
//        @Expose
//        private String name;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//    }
//    public class Parent {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//    }
//    public class Priority {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//        @SerializedName("name")
//        @Expose
//        private String name;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//    }
//    public class Project {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//        @SerializedName("name")
//        @Expose
//        private String name;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//    }
//    public class CustomField {
//
//        @SerializedName("id")
//        @Expose
//        private Integer id;
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("name")
//        @Expose
//        private String name;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//    }
}
