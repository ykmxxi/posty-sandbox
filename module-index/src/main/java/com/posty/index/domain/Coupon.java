package com.posty.index.domain;

import java.time.LocalDateTime;

import com.posty.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
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

    public Coupon(String name, Integer discountAmount, Integer quantity,
        LocalDateTime issueStartedAt, LocalDateTime issueEndedAt, String status) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.issueStartedAt = issueStartedAt;
        this.issueEndedAt = issueEndedAt;
        this.status = status;
    }
}
