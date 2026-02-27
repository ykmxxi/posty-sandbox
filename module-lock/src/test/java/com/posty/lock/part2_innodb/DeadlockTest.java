package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.CouponRepository;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: 데드락 재현 및 분석 - SHOW ENGINE INNODB STATUS")
class DeadlockTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("교차 순서 UPDATE로 데드락을 재현한다")
    void deadlock_crossOrder_detected() {
        // TODO: 구현
        // 1. 쿠폰 2개 생성 (id=1, id=2)
        // 2. 세션 A: UPDATE id=1 → UPDATE id=2 (id=2에서 대기)
        // 3. 세션 B: UPDATE id=2 → UPDATE id=1 (id=1에서 대기 → 데드락!)
        // 4. InnoDB가 하나의 트랜잭션을 롤백시킴
    }

    @Test
    @DisplayName("SHOW ENGINE INNODB STATUS에서 LATEST DETECTED DEADLOCK 정보를 확인한다")
    void deadlock_innodbStatus_showsDeadlockInfo() {
        // TODO: 구현
        // 1. 데드락 발생시킨 후
        // 2. SHOW ENGINE INNODB STATUS 실행
        // 3. LATEST DETECTED DEADLOCK 섹션에서 관련 트랜잭션 정보 확인
    }

    @Test
    @DisplayName("innodb_lock_wait_timeout을 줄여 빠른 실패를 유도할 수 있다")
    void deadlock_lockWaitTimeout_quickFail() {
        // TODO: 구현
        // 1. SET SESSION innodb_lock_wait_timeout = 5 (기본 50초 → 5초)
        // 2. 락 대기 상황 발생
        // 3. 5초 후 Lock wait timeout exceeded 에러 발생 확인
    }

    @Test
    @DisplayName("리소스 접근 순서를 동일하게 하면 데드락을 예방할 수 있다")
    void deadlock_sameOrder_prevented() {
        // TODO: 구현
        // 1. 쿠폰 2개 생성 (id=1, id=2)
        // 2. 세션 A: UPDATE id=1 → UPDATE id=2 (PK 오름차순)
        // 3. 세션 B: UPDATE id=1 → UPDATE id=2 (PK 오름차순)
        // 4. 데드락 없이 순차 실행됨을 확인
    }
}
