package com.posty.transaction;

import com.posty.transaction.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("READ UNCOMMITTED에서 Dirty Read 재현")
class DirtyReadTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("READ UNCOMMITTED에서 커밋되지 않은 데이터를 읽을 수 있다 (Dirty Read)")
    void readUncommitted_shouldSeeDirtyData() {
        // TODO: 세션 A(READ UNCOMMITTED)에서 세션 B가 커밋하지 않은 변경을 읽는다
        // 1. 초기 데이터 삽입
        // 2. 세션 B: 트랜잭션 시작 → UPDATE → 커밋하지 않음
        // 3. 세션 A: READ UNCOMMITTED로 SELECT → 커밋되지 않은 데이터가 보인다
        // 4. 세션 B: ROLLBACK
        // 5. 세션 A: SELECT → 데이터가 원래대로 돌아왔다
    }

    @Test
    @DisplayName("READ COMMITTED에서는 커밋되지 않은 데이터를 읽을 수 없다")
    void readCommitted_shouldNotSeeDirtyData() {
        // TODO: 동일 시나리오에서 READ COMMITTED로 변경 → Dirty Read 발생하지 않음 확인
    }
}
