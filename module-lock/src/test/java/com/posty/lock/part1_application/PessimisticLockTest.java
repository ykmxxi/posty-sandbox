package com.posty.lock.part1_application;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.CouponRepository;
import com.posty.lock.service.PessimisticLockCouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part1: 비관적 락 (Pessimistic Lock) - SELECT ... FOR UPDATE 기반 동시성 제어")
class PessimisticLockTest extends BaseIntegrationTest {

    @Autowired
    private PessimisticLockCouponService pessimisticLockCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("비관적 락으로 100개 스레드가 동시에 차감해도 재고가 정확히 0이 된다")
    void pessimisticLock_concurrentUpdate_exactResult() {
        // TODO: 구현
        // 1. 쿠폰 생성 (quantity = 100)
        // 2. ExecutorService로 100개 스레드 동시 차감
        // 3. 최종 재고가 정확히 0인지 확인
    }

    @Test
    @DisplayName("SELECT ... FOR UPDATE 쿼리가 실제로 실행되는지 확인한다")
    void pessimisticLock_selectForUpdate_queryGenerated() {
        // TODO: 구현
        // 1. 쿠폰 생성
        // 2. 비관적 락으로 조회
        // 3. p6spy 로그에서 SELECT ... FOR UPDATE 쿼리 확인
    }

    @Test
    @DisplayName("비관적 락 획득 시 performance_schema.data_locks에서 X Lock을 확인할 수 있다")
    void pessimisticLock_dataLocks_showsExclusiveLock() {
        // TODO: 구현
        // 1. 쿠폰 생성
        // 2. 트랜잭션 내에서 비관적 락 획득
        // 3. LockMonitor로 data_locks 조회하여 X,REC_NOT_GAP 확인
    }
}
