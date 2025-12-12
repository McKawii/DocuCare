package com.example.todo.models;

import com.google.firebase.Timestamp;

public class ExamMessage {

    private String id;
    private long examId;
    private String title;
    private String content;
    private Timestamp createdAt;

    public ExamMessage() {
    }

    public ExamMessage(long examId, String title, String content, Timestamp createdAt) {
        this.examId = examId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getExamId() {
        return examId;
    }

    public void setExamId(long examId) {
        this.examId = examId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
