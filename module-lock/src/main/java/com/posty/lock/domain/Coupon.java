package com.posty.lock.domain;

import com.posty.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer quantity;

    @Version
    private Long version;

    protected Coupon() {
    }

    public Coupon(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getVersion() {
        return version;
    }

    public void decreaseQuantity() {
        if (this.quantity <= 0) {
            throw new IllegalStateException("쿠폰 재고가 없습니다.");
        }
        this.quantity -= 1;
    }
}
