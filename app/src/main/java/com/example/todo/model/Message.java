package com.example.todo.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String id;
    private String title;
    private String description;
    private String category; // "vaccinations", "examinations", "appointments"
    private Date date;
    private boolean isRead;

    public Message(String id, String title, String description, String category, Date date, boolean isRead) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.date = date;
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return id.equals(message.id);
    }
}

