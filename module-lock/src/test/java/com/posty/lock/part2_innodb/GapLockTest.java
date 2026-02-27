package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: Gap Lock - 인덱스 레코드 사이 갭에 거는 락 (Phantom Read 방지)")
class GapLockTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("존재하지 않는 값에 FOR UPDATE를 걸면 해당 갭에 Gap Lock이 걸린다")
    void gapLock_nonExistentValue_locksGap() {
        // TODO: 구현
        // 1. 인덱스가 있는 컬럼에 값 10, 20, 30 삽입
        // 2. WHERE indexed_col = 15 FOR UPDATE (값 15는 존재하지 않음)
        // 3. data_locks에서 LOCK_MODE = X,GAP 확인
        // 4. 다른 세션에서 INSERT indexed_col = 12 → 대기
    }

    @Test
    @DisplayName("Gap Lock은 INSERT만 차단하고 다른 Gap Lock과는 호환된다")
    void gapLock_compatible_withOtherGapLocks() {
        // TODO: 구현
        // 1. 두 트랜잭션이 같은 갭에 Gap Lock을 동시에 잡을 수 있음을 확인
        // 2. Gap Lock이 걸린 상태에서 INSERT 시도 → 대기
    }

    @Test
    @DisplayName("Gap Lock은 REPEATABLE READ에서 Phantom Read를 방지한다")
    void gapLock_preventsPhantomRead() {
        // TODO: 구현
        // 1. 트랜잭션 A: 범위 쿼리 실행 (Gap Lock 획득)
        // 2. 트랜잭션 B: 해당 범위에 INSERT 시도 → 대기
        // 3. Phantom Read가 방지됨을 확인
    }
}
