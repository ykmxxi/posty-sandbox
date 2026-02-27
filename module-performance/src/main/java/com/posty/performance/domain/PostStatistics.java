package com.posty.performance.domain;

import com.posty.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PostStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId;

    private Integer postCount;

    protected PostStatistics() {
    }

    public PostStatistics(Long userId, Integer postCount) {
        this.userId = userId;
        this.postCount = postCount;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getPostCount() {
        return postCount;
    }
}
