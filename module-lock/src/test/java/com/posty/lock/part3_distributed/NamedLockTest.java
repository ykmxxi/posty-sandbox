package com.posty.lock.part3_distributed;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.CouponRepository;
import com.posty.lock.repository.NamedLockRepository;
import com.posty.lock.service.NamedLockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part3: Named Lock (User-Level Lock) - GET_LOCK / RELEASE_LOCK 기반 동시성 제어")
class NamedLockTest extends BaseIntegrationTest {

    @Autowired
    private NamedLockService namedLockService;

    @Autowired
    private NamedLockRepository namedLockRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("GET_LOCK으로 Named Lock을 획득하고 RELEASE_LOCK으로 해제할 수 있다")
    void namedLock_getLockAndRelease_success() {
        // TODO: 구현
        // 1. GET_LOCK('test_lock', 10) → 반환값 1 (성공)
        // 2. RELEASE_LOCK('test_lock') → 반환값 1 (성공)
    }

    @Test
    @DisplayName("다른 세션이 같은 이름의 Named Lock을 잡고 있으면 대기한다")
    void namedLock_sameName_waits() {
        // TODO: 구현
        // 1. 세션 A: GET_LOCK('coupon_issue', 10) → 획득
        // 2. 세션 B: GET_LOCK('coupon_issue', 3) → 3초 대기 후 타임아웃 (반환값 0)
        // 3. 세션 A: RELEASE_LOCK → 세션 B 재시도 시 획득 가능
    }

    @Test
    @DisplayName("Named Lock 기반 쿠폰 발급 직렬화로 동시성 문제를 해결한다")
    void namedLock_couponIssue_serialized() {
        // TODO: 구현
        // 1. 쿠폰 생성 (quantity = 100)
        // 2. 100개 스레드가 Named Lock으로 직렬화하여 재고 차감
        // 3. 최종 재고가 정확히 0인지 확인
    }

    @Test
    @DisplayName("Named Lock은 트랜잭션 커밋/롤백과 독립적으로 동작한다")
    void namedLock_independentOfTransaction() {
        // TODO: 구현
        // 1. GET_LOCK 획득
        // 2. 트랜잭션 시작 → 비즈니스 로직 실행 → 커밋
        // 3. 트랜잭션 커밋 후에도 Named Lock은 여전히 유지됨
        // 4. 반드시 RELEASE_LOCK으로 명시적 해제 필요
    }
}
