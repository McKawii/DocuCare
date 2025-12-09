package com.example.todo.model;

import java.io.Serializable;
import java.util.List;

public class KnowledgeArticle implements Serializable {
    private String id;
    private String categoryId;
    private String title;
    private String categoryBadge;
    private int readTimeMinutes;
    private String updateDate;
    private String content;
    private List<String> importantPoints;
    private List<ExternalLink> usefulLinks;
    private String disclaimer;

    public KnowledgeArticle(String id, String categoryId, String title, String categoryBadge, 
                           int readTimeMinutes, String updateDate, String content, 
                           List<String> importantPoints, List<ExternalLink> usefulLinks, String disclaimer) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.categoryBadge = categoryBadge;
        this.readTimeMinutes = readTimeMinutes;
        this.updateDate = updateDate;
        this.content = content;
        this.importantPoints = importantPoints;
        this.usefulLinks = usefulLinks;
        this.disclaimer = disclaimer;
    }

    public String getId() {
        return id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategoryBadge() {
        return categoryBadge;
    }

    public int getReadTimeMinutes() {
        return readTimeMinutes;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getContent() {
        return content;
    }

    public List<String> getImportantPoints() {
        return importantPoints;
    }

    public List<ExternalLink> getUsefulLinks() {
        return usefulLinks;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public static class ExternalLink implements Serializable {
        private String title;
        private String url;

        public ExternalLink(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }
    }
}

