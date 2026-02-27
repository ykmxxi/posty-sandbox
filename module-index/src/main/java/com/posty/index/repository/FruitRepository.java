package com.posty.index.repository;

import com.posty.index.domain.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FruitRepository extends JpaRepository<Fruit, Long> {
}
