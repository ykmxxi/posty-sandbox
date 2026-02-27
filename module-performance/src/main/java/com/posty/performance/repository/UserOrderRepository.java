package com.posty.performance.repository;

import com.posty.performance.domain.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
}
