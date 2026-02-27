package com.posty.transaction;

import com.posty.transaction.service.PropagationDemoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("트랜잭션 전파 속성(Propagation) 실습")
class PropagationTest extends BaseIntegrationTest {

    @Autowired
    private PropagationDemoService propagationDemoService;

    @Test
    @DisplayName("REQUIRES_NEW는 외부 트랜잭션과 별도의 커넥션을 사용한다")
    void requiresNew_shouldUseNewConnection() {
        // TODO: 외부 트랜잭션 내에서 REQUIRES_NEW 호출 시 별도 커넥션 사용 확인
    }

    @Test
    @DisplayName("REQUIRES_NEW에서 HikariCP pool size가 부족하면 커넥션 풀 고갈(데드락)이 발생한다")
    void requiresNew_withSmallPool_shouldCauseConnectionPoolExhaustion() {
        // TODO: HikariCP pool size를 1로 설정 → 외부 트랜잭션 + 내부 REQUIRES_NEW → 데드락 재현
        // 외부 트랜잭션이 커넥션 1개를 점유한 상태에서
        // 내부 REQUIRES_NEW가 새 커넥션을 요청 → pool에 여유 커넥션 없음 → 타임아웃/데드락
    }

    @Test
    @DisplayName("REQUIRED(기본값)는 기존 트랜잭션에 참여하여 같은 커넥션을 사용한다")
    void required_shouldParticipateInExistingTransaction() {
        // TODO: REQUIRED 전파 속성에서 외부/내부 트랜잭션이 같은 커넥션을 공유하는지 확인
    }
}
