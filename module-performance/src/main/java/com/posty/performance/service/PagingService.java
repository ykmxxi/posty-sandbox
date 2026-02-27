package com.posty.performance.service;

import com.posty.performance.domain.Coupon;
import com.posty.performance.repository.CouponRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagingService {

    private final CouponRepository couponRepository;
    private final EntityManager entityManager;

    public PagingService(CouponRepository couponRepository, EntityManager entityManager) {
        this.couponRepository = couponRepository;
        this.entityManager = entityManager;
    }

    /**
     * Offset 기반 페이징.
     * LIMIT + OFFSET 쿼리를 사용한다. 페이지가 뒤로 갈수록 느려진다.
     * TODO: couponRepository.findAllByOrderByIdAsc(PageRequest.of(page, size)) 호출
     */
    public Page<Coupon> findByOffsetPaging(int page, int size) {
        // TODO: Offset 페이징 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }

    /**
     * Cursor 기반 페이징 (Keyset Pagination).
     * 마지막으로 조회한 id를 기준으로 다음 데이터를 조회한다.
     * TODO: couponRepository.findByIdGreaterThanOrderByIdAsc(lastId, size) 호출
     */
    public List<Coupon> findByCursorPaging(Long lastId, int size) {
        // TODO: Cursor 페이징 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }

    /**
     * Deferred Join 패턴으로 Offset 성능을 개선한다.
     * 서브쿼리에서 PK만 먼저 추출한 뒤, 본 쿼리에서 해당 PK로 데이터를 조회한다.
     * TODO: EntityManager로 네이티브 쿼리 실행
     *   SELECT c.* FROM coupon c
     *   JOIN (SELECT id FROM coupon ORDER BY id LIMIT :size OFFSET :offset) sub
     *   ON c.id = sub.id
     */
    @SuppressWarnings("unchecked")
    public List<Coupon> findByDeferredJoin(int offset, int size) {
        // TODO: Deferred Join 네이티브 쿼리 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }
}
