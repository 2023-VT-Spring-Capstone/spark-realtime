package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;

public class News {
    private int id;
    private String symbol;
    private String uuid;
    private String title;
    private String publisher;
    private String link;
    private LocalDateTime publishTime;
    private String newsType;
    private String thumbnail;
    private String relatedTickers;
    private LocalDateTime createdAt;

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getLink() {
        return link;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public String getNewsType() {
        return newsType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getRelatedTickers() {
        return relatedTickers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
