package com.posty.transaction;

import com.posty.transaction.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("SERIALIZABLE 격리 수준 동작 확인")
class SerializableTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("SERIALIZABLE에서는 일반 SELECT도 공유 락(S Lock)을 획득한다")
    void serializable_selectAcquiresSharedLock() {
        // TODO: SERIALIZABLE에서 SELECT가 LOCK IN SHARE MODE처럼 동작하는지 확인
        // 1. 초기 데이터 삽입
        // 2. 세션 A: SERIALIZABLE 트랜잭션 시작 → SELECT (S Lock 획득)
        // 3. 세션 B: 같은 행 UPDATE 시도 → 대기 또는 타임아웃
    }

    @Test
    @DisplayName("SERIALIZABLE에서 두 세션이 같은 범위를 읽으면 UPDATE 시 데드락이 발생할 수 있다")
    void serializable_concurrentReadThenUpdate_shouldDeadlock() {
        // TODO: 두 세션이 같은 범위를 읽고 한쪽이 UPDATE → 데드락 또는 대기 발생 확인
    }
}
