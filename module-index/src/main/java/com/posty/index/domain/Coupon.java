package com.posty.index.domain;

import com.posty.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer discountAmount;

    private Integer quantity;

    private LocalDateTime issueStartedAt;

    private LocalDateTime issueEndedAt;

    private String status;

    protected Coupon() {
    }

    public Coupon(String name, Integer discountAmount, Integer quantity,
                  LocalDateTime issueStartedAt, LocalDateTime issueEndedAt, String status) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.issueStartedAt = issueStartedAt;
        this.issueEndedAt = issueEndedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getIssueStartedAt() {
        return issueStartedAt;
    }

    public LocalDateTime getIssueEndedAt() {
        return issueEndedAt;
    }

    public String getStatus() {
        return status;
    }
}
