package com.example.todo.model;

import java.io.Serializable;

public class KnowledgeCategory implements Serializable {
    private String id;
    private String title;
    private String description;
    private int articleCount;
    private int iconResId;
    private int colorResId;
    private boolean isExternalLink;

    public KnowledgeCategory(String id, String title, String description, int articleCount, int iconResId, int colorResId, boolean isExternalLink) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.articleCount = articleCount;
        this.iconResId = iconResId;
        this.colorResId = colorResId;
        this.isExternalLink = isExternalLink;
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

    public int getArticleCount() {
        return articleCount;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getColorResId() {
        return colorResId;
    }

    public boolean isExternalLink() {
        return isExternalLink;
    }
}

