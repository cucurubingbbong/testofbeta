package com.jrm.app.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommunityPost {
    private final String id;
    private final String author;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    public CommunityPost(String author, String title, String content) {
        this.id = UUID.randomUUID().toString();
        this.author = author;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
