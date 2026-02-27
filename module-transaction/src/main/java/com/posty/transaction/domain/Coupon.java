package com.posty.transaction.domain;

import com.posty.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer discountAmount;

    private String status;

    protected Coupon() {
    }

    public Coupon(String name, Integer discountAmount, String status) {
        this.name = name;
        this.discountAmount = discountAmount;
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

    public String getStatus() {
        return status;
    }
}
