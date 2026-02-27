package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: Intention Lock (IS/IX) - 테이블 수준 의도 락")
class IntentionLockTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("행 락을 잡기 전에 테이블에 IS/IX 의도 락이 먼저 걸린다")
    void intentionLock_beforeRowLock_tableIntentionLockAcquired() {
        // TODO: 구현
        // 1. SELECT ... FOR SHARE → IS Lock이 테이블에 먼저 걸림
        // 2. SELECT ... FOR UPDATE → IX Lock이 테이블에 먼저 걸림
        // 3. data_locks에서 LOCK_TYPE=TABLE, LOCK_MODE=IS/IX 확인
    }

    @Test
    @DisplayName("IS/IX Lock끼리는 호환되어 행 수준 동시성을 허용한다")
    void intentionLock_compatible_allowsConcurrency() {
        // TODO: 구현
        // 1. 세션 A: SELECT ... FOR UPDATE (id=1) → IX Lock + Row X Lock
        // 2. 세션 B: SELECT ... FOR UPDATE (id=2) → IX Lock + Row X Lock
        // 3. 두 세션 모두 성공 (IX-IX 호환, 행 락은 다른 행이므로 충돌 없음)
    }
}
