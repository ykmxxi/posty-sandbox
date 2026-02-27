package com.posty.lock.part1_application;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part1: Second Lost Update Problem - 락 없이 동시 수정 시 갱신 분실 재현")
class SecondLostUpdateTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("락 없이 동시에 재고를 차감하면 Lost Update가 발생하여 재고가 0이 되지 않는다")
    void noLock_concurrentUpdate_lostUpdate() {
        // TODO: 구현
        // 1. 쿠폰 생성 (quantity = 100)
        // 2. 락 없이 100개 스레드 동시 차감
        // 3. 최종 재고가 0보다 큰 것을 확인 (Lost Update 발생)
    }

    @Test
    @DisplayName("read → 계산 → write 사이에 다른 트랜잭션이 끼어들면 갱신이 분실된다")
    void readComputeWrite_interleaved_updateLost() {
        // TODO: 구현
        // 1. 쿠폰 생성 (quantity = 10)
        // 2. 스레드 A: read(quantity=10) → 계산(10-1=9) → write(9)
        // 3. 스레드 B: read(quantity=10) → 계산(10-1=9) → write(9)
        // 4. 기대값: 8, 실제값: 9 → 한 번의 차감이 분실됨
    }
}
