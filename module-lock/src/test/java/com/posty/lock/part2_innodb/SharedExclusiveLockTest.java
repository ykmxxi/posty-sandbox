package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.CouponRepository;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: S Lock / X Lock - 공유 락과 배타 락 동작 확인")
class SharedExclusiveLockTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("여러 트랜잭션이 동시에 S Lock(FOR SHARE)을 잡을 수 있다")
    void sharedLock_multipleSessions_compatible() {
        // TODO: 구현
        // 1. 쿠폰 생성
        // 2. 세션 A: SELECT ... FOR SHARE → S Lock 획득
        // 3. 세션 B: SELECT ... FOR SHARE → 성공 (S-S 호환)
    }

    @Test
    @DisplayName("S Lock이 걸린 행에 UPDATE(X Lock)를 시도하면 대기한다")
    void sharedLock_exclusiveLockRequest_waits() {
        // TODO: 구현
        // 1. 쿠폰 생성
        // 2. 세션 A: SELECT ... FOR SHARE → S Lock 획득
        // 3. 세션 B: UPDATE ... → X Lock 요청 → 대기 (S-X 비호환)
    }

    @Test
    @DisplayName("X Lock은 다른 모든 락(S, X)과 충돌한다")
    void exclusiveLock_conflictsWithAll() {
        // TODO: 구현
        // 1. 쿠폰 생성
        // 2. 세션 A: SELECT ... FOR UPDATE → X Lock 획득
        // 3. 세션 B: SELECT ... FOR SHARE → 대기 (X-S 비호환)
        // 4. 세션 C: SELECT ... FOR UPDATE → 대기 (X-X 비호환)
    }
}
