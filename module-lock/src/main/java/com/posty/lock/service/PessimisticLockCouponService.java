package com.posty.lock.service;

import com.posty.lock.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockCouponService {

    private final CouponRepository couponRepository;

    public PessimisticLockCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * 비관적 락(SELECT ... FOR UPDATE)을 사용한 쿠폰 재고 차감.
     * 동시 접근 시 락을 획득할 때까지 대기하므로 직렬화된다.
     *
     * TODO: 비관적 락 기반 쿠폰 재고 차감 구현
     */
    @Transactional
    public void decreaseQuantity(Long couponId) {
        // TODO: 비관적 락 기반 쿠폰 재고 차감 구현
        // 1. couponRepository.findByIdWithPessimisticLock(couponId) → SELECT ... FOR UPDATE
        // 2. coupon.decreaseQuantity()
        // 3. 트랜잭션 커밋 시 락 해제
    }
}
