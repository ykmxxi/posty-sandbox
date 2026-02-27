package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: Metadata Lock (MDL) - DDL과 DML 간 충돌 방지")
class MetadataLockTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("DML 실행 중 DDL(ALTER TABLE)을 시도하면 Metadata Lock에 의해 대기한다")
    void metadataLock_dmlRunning_ddlWaits() {
        // TODO: 구현
        // 1. 세션 A: BEGIN → SELECT * FROM coupon WHERE id = 1 (트랜잭션 유지)
        // 2. 세션 B: ALTER TABLE coupon ADD COLUMN description TEXT → 대기 (MDL)
        // 3. 세션 A: COMMIT → 세션 B의 ALTER 진행
    }

    @Test
    @DisplayName("DDL 실행 중 새로운 DML도 대기한다 (MDL 큐잉)")
    void metadataLock_ddlWaiting_newDmlAlsoWaits() {
        // TODO: 구현
        // 1. 세션 A: 장시간 트랜잭션 유지 (DML)
        // 2. 세션 B: ALTER TABLE 시도 → MDL 대기
        // 3. 세션 C: SELECT * FROM coupon → 세션 B의 DDL MDL 뒤에 큐잉되어 대기
        // ⚠️ 운영 중 DDL 실행 시 주의해야 하는 이유
    }
}
