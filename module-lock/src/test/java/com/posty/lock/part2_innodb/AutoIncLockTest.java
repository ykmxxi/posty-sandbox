package com.posty.lock.part2_innodb;

import com.posty.lock.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part2: Auto-Inc Lock - AUTO_INCREMENT 컬럼에 대한 테이블 수준 락")
class AutoIncLockTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("innodb_autoinc_lock_mode 설정값에 따라 AUTO_INCREMENT 채번 방식이 달라진다")
    void autoIncLock_lockMode_affectsBehavior() {
        // TODO: 구현
        // 1. SHOW VARIABLES LIKE 'innodb_autoinc_lock_mode' 확인
        // 2. mode 0: 전통적 테이블 수준 락 (INSERT 완료까지 보유)
        // 3. mode 1: 연속 모드 (단순 INSERT는 경량 뮤텍스, bulk INSERT는 테이블 락)
        // 4. mode 2: 인터리브 모드 (항상 경량 뮤텍스, 가장 높은 동시성)
    }

    @Test
    @DisplayName("동시 INSERT 시 AUTO_INCREMENT 값이 연속적이지 않을 수 있다 (mode=2)")
    void autoIncLock_concurrentInsert_nonConsecutiveIds() {
        // TODO: 구현
        // 1. 여러 스레드에서 동시 INSERT
        // 2. 생성된 ID 값들이 연속적인지 확인
        // 3. innodb_autoinc_lock_mode=2에서는 갭이 발생할 수 있음
    }
}
