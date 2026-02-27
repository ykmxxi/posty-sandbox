package com.posty.performance.domain;

import com.posty.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;

@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    @Lob
    private String content;

    protected Post() {
    }

    public Post(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
