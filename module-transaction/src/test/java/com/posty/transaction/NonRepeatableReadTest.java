package com.posty.transaction;

import com.posty.transaction.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("READ COMMITTED에서 Non-Repeatable Read 재현")
class NonRepeatableReadTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("READ COMMITTED에서 다른 트랜잭션의 커밋된 변경이 보인다 (Non-Repeatable Read)")
    void readCommitted_shouldSeeCommittedChanges() {
        // TODO: 세션 A(READ COMMITTED)에서 같은 SELECT를 두 번 실행 → 결과가 달라진다
        // 1. 초기 데이터 삽입
        // 2. 세션 A: READ COMMITTED 트랜잭션 시작 → 1차 SELECT (결과 X)
        // 3. 세션 B: 같은 행 UPDATE → COMMIT
        // 4. 세션 A: 2차 SELECT → 결과가 달라짐 (Non-Repeatable Read)
    }

    @Test
    @DisplayName("REPEATABLE READ에서는 같은 트랜잭션 내에서 일관된 결과를 반환한다")
    void repeatableRead_shouldReturnConsistentResult() {
        // TODO: 동일 시나리오에서 REPEATABLE READ → 1차/2차 SELECT 결과가 동일함을 확인
    }
}
