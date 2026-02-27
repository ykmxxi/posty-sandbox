package com.posty.lock.domain;

import com.posty.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer price;

    private Boolean isSoldOut;

    private String category;

    protected Menu() {
    }

    public Menu(String name, Integer price, Boolean isSoldOut, String category) {
        this.name = name;
        this.price = price;
        this.isSoldOut = isSoldOut;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Boolean getIsSoldOut() {
        return isSoldOut;
    }

    public String getCategory() {
        return category;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void markSoldOut() {
        this.isSoldOut = true;
    }
}
