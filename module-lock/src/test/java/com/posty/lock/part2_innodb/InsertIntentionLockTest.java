package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: Insert Intention Lock - INSERT 시 Gap Lock과의 충돌 해소")
class InsertIntentionLockTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("같은 갭에 서로 다른 값을 INSERT하면 Insert Intention Lock끼리 충돌하지 않는다")
    void insertIntentionLock_differentValues_noConflict() {
        // TODO: 구현
        // 1. 인덱스 값 10, 20 존재 (10~20 갭)
        // 2. 세션 A: INSERT indexed_col = 12
        // 3. 세션 B: INSERT indexed_col = 15
        // 4. 두 INSERT 모두 성공 (Insert Intention Lock끼리 호환)
    }

    @Test
    @DisplayName("Gap Lock이 걸린 상태에서 INSERT하면 Insert Intention Lock이 대기한다")
    void insertIntentionLock_gapLockExists_waits() {
        // TODO: 구현
        // 1. 세션 A: 범위 쿼리로 Gap Lock 획득
        // 2. 세션 B: 해당 갭에 INSERT 시도 → Insert Intention Lock이 Gap Lock과 충돌 → 대기
    }
}
