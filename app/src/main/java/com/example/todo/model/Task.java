package com.example.todo.model;

import androidx.room.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "TaskTable")
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    private int id;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Description")
    private String description;

    @ColumnInfo(name = "CreateTime")
    private Date createTime;

    @ColumnInfo(name = "ModifyTime")
    private Date modifyTime;

    @ColumnInfo(name = "DueTime")
    private Date dueTime;

    @ColumnInfo(name = "IsCompleted")
    private boolean isCompleted;

    @ColumnInfo(name = "IsNotificationEnabled")
    private boolean isNotificationEnabled;

    @ColumnInfo(name = "Category")
    private String category;

    @ColumnInfo(name = "AttachmentsPaths")
    private List<String> attachmentsPaths;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        isNotificationEnabled = notificationEnabled;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getAttachmentsPaths() {
        return attachmentsPaths;
    }

    public void setAttachmentsPaths(List<String> attachmentsPaths) {
        this.attachmentsPaths = attachmentsPaths;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return id == task.id &&
                isCompleted == task.isCompleted &&
                isNotificationEnabled == task.isNotificationEnabled &&
                (Objects.equals(title, task.title)) &&
                (Objects.equals(description, task.description)) &&
                (Objects.equals(createTime, task.createTime)) &&
                (Objects.equals(modifyTime, task.modifyTime)) &&
                (Objects.equals(dueTime, task.dueTime)) &&
                (Objects.equals(category, task.category)) &&
                (Objects.equals(attachmentsPaths, task.attachmentsPaths));
    }
}