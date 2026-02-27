package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: Next-Key Lock - Record Lock + Gap Lock (InnoDB REPEATABLE READ 기본 잠금)")
class NextKeyLockTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("범위 조건 FOR UPDATE 시 Next-Key Lock이 걸린다 (Record + Gap)")
    void nextKeyLock_rangeCondition_locksRecordAndGap() {
        // TODO: 구현
        // 1. 인덱스 값 10, 20, 30, 40 삽입
        // 2. SELECT ... WHERE indexed_col BETWEEN 15 AND 25 FOR UPDATE
        // 3. data_locks에서 Next-Key Lock 범위 확인: (10, 20], (20, 30]
    }

    @Test
    @DisplayName("Next-Key Lock 범위 내에 INSERT하면 대기한다")
    void nextKeyLock_insertInRange_waits() {
        // TODO: 구현
        // 1. Next-Key Lock 획득 (범위: 10~30)
        // 2. INSERT indexed_col = 11 → 대기 (Gap Lock에 의해)
        // 3. INSERT indexed_col = 25 → 대기 (Gap Lock에 의해)
    }

    @Test
    @DisplayName("Next-Key Lock 범위 밖의 행은 정상적으로 수정 가능하다")
    void nextKeyLock_outsideRange_notBlocked() {
        // TODO: 구현
        // 1. Next-Key Lock 획득 (범위: 10~30)
        // 2. UPDATE indexed_col = 40 → 성공 (범위 밖)
    }
}
