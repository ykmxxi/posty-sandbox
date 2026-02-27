package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.CouponRepository;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: Record Lock - 인덱스 레코드에 거는 락")
class RecordLockTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("PK 조건으로 FOR UPDATE 시 해당 레코드에만 X,REC_NOT_GAP 락이 걸린다")
    void recordLock_pkCondition_locksOnlyTargetRecord() {
        // TODO: 구현
        // 1. 쿠폰 여러 개 생성
        // 2. SELECT ... WHERE id = 1 FOR UPDATE
        // 3. performance_schema.data_locks에서 LOCK_MODE = X,REC_NOT_GAP 확인
        // 4. 다른 행(id = 2)은 정상적으로 수정 가능한지 확인
    }

    @Test
    @DisplayName("Record Lock은 인덱스 레코드에 걸리므로 data_locks에서 LOCK_DATA로 확인할 수 있다")
    void recordLock_dataLocks_showsLockedRecord() {
        // TODO: 구현
        // 1. 쿠폰 생성
        // 2. FOR UPDATE로 락 획득
        // 3. LockMonitor로 data_locks 조회
        // 4. LOCK_TYPE=RECORD, LOCK_DATA=PK값 확인
    }
}
