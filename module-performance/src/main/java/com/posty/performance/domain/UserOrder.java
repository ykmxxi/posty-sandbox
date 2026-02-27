package com.posty.performance.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long totalAmount;

    private String status;

    private LocalDateTime orderedAt;

    protected UserOrder() {
    }

    public UserOrder(Long userId, Long totalAmount, String status, LocalDateTime orderedAt) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderedAt = orderedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }
}
