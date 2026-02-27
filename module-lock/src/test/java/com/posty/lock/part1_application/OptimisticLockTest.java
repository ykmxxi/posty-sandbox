package com.posty.lock.part1_application;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.CouponRepository;
import com.posty.lock.service.OptimisticLockCouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part1: 낙관적 락 (Optimistic Lock) - @Version 기반 동시성 제어")
class OptimisticLockTest extends BaseIntegrationTest {

    @Autowired
    private OptimisticLockCouponService optimisticLockCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("동시에 같은 쿠폰 재고를 차감하면 버전 충돌로 ObjectOptimisticLockingFailureException이 발생한다")
    void optimisticLock_concurrentUpdate_throwsVersionConflict() {
        // TODO: 구현
        // 1. 쿠폰 생성 (quantity = 100)
        // 2. ExecutorService로 100개 스레드 동시 차감
        // 3. ObjectOptimisticLockingFailureException 발생 확인
        // 4. 재고가 0이 아님을 확인 (일부 요청이 실패했으므로)
    }

    @Test
    @DisplayName("낙관적 락 + 재시도 로직으로 모든 차감 요청이 성공한다")
    void optimisticLock_withRetry_allRequestsSucceed() {
        // TODO: 구현
        // 1. 쿠폰 생성 (quantity = 100)
        // 2. ExecutorService로 100개 스레드 동시 차감 (재시도 포함)
        // 3. 최종 재고가 0인지 확인
    }

    @Test
    @DisplayName("@Version에 의해 UPDATE 쿼리에 version 조건이 포함된다")
    void optimisticLock_updateQuery_containsVersionCondition() {
        // TODO: 구현
        // 1. 쿠폰 생성
        // 2. 재고 차감 수행
        // 3. p6spy 로그에서 UPDATE ... WHERE id = ? AND version = ? 쿼리 확인
    }
}
