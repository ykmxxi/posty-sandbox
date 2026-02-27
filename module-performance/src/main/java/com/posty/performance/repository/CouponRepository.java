package com.posty.performance.repository;

import com.posty.performance.domain.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * Offset 기반 페이징.
     * Spring Data의 Pageable을 사용하여 LIMIT + OFFSET 쿼리를 생성한다.
     */
    Page<Coupon> findAllByOrderByIdAsc(Pageable pageable);

    /**
     * Cursor 기반 페이징 (Keyset Pagination).
     * 마지막으로 조회한 id 이후의 데이터를 조회한다.
     */
    @Query("SELECT c FROM Coupon c WHERE c.id > :lastId ORDER BY c.id ASC LIMIT :size")
    List<Coupon> findByIdGreaterThanOrderByIdAsc(@Param("lastId") Long lastId, @Param("size") int size);
}
