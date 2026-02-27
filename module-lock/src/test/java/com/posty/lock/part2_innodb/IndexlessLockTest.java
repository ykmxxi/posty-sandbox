package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.util.LockMonitor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: 인덱스 없는 컬럼의 락 - Full Table Scan으로 인한 전체 행 잠금")
class IndexlessLockTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LockMonitor lockMonitor;

    @Test
    @DisplayName("인덱스 없는 컬럼에 FOR UPDATE를 걸면 테이블 전체 행에 락이 걸린다")
    void indexlessColumn_forUpdate_locksAllRows() {
        // TODO: 구현
        // 1. 여러 쿠폰 생성 (인덱스 없는 name 컬럼 사용)
        // 2. SELECT * FROM coupon WHERE name = 'A' FOR UPDATE
        // 3. data_locks 조회 → 모든 행에 Next-Key Lock 확인
        // 4. 다른 세션에서 name = 'B'인 행 수정 시도 → 대기!
    }

    @Test
    @DisplayName("인덱스를 추가하면 해당 행만 잠기고 다른 행은 수정 가능하다")
    void withIndex_forUpdate_locksOnlyMatchingRows() {
        // TODO: 구현
        // 1. name 컬럼에 인덱스 추가
        // 2. SELECT * FROM coupon WHERE name = 'A' FOR UPDATE
        // 3. data_locks 조회 → 해당 행만 락 확인
        // 4. 다른 세션에서 name = 'B'인 행 수정 → 성공
    }
}
