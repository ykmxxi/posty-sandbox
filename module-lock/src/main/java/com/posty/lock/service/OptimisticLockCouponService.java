package com.posty.lock.service;

import com.posty.lock.domain.Coupon;
import com.posty.lock.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticLockCouponService {

    private final CouponRepository couponRepository;

    public OptimisticLockCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * 낙관적 락을 사용한 쿠폰 재고 차감.
     * @Version 필드를 통해 동시 수정 시 ObjectOptimisticLockingFailureException 발생.
     * 호출부에서 재시도 로직을 구현해야 한다.
     *
     * TODO: 재시도 로직 구현 (while 루프 또는 @Retryable)
     */
    @Transactional
    public void decreaseQuantity(Long couponId) {
        // TODO: 낙관적 락 기반 쿠폰 재고 차감 구현
        // 1. couponRepository.findById(couponId)
        // 2. coupon.decreaseQuantity()
        // 3. @Version에 의해 UPDATE ... WHERE id = ? AND version = ? 쿼리 발생
    }

    /**
     * 재시도 로직을 포함한 낙관적 락 쿠폰 차감.
     *
     * TODO: ObjectOptimisticLockingFailureException 발생 시 재시도 구현
     */
    public void decreaseQuantityWithRetry(Long couponId) {
        // TODO: 재시도 로직 구현
        // while (true) {
        //     try {
        //         decreaseQuantity(couponId);
        //         break;
        //     } catch (ObjectOptimisticLockingFailureException e) {
        //         Thread.sleep(50); // 짧은 대기 후 재시도
        //     }
        // }
    }
}
