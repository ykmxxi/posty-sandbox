package com.posty.replication;

import com.posty.replication.repository.CouponRepository;
import com.posty.replication.service.CouponService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("복제 지연 재현 테스트")
class ReplicationLagTest extends BaseIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Source에 INSERT 후 Replica에서 즉시 SELECT하면 복제 지연으로 데이터가 보이지 않을 수 있다")
    void replicationLagCausesStaleRead() {
        // TODO: 복제 지연 재현
        // 1. Replica SQL Thread 일시 정지 (STOP REPLICA SQL_THREAD)
        // 2. Source에 쿠폰 INSERT
        // 3. Replica에서 즉시 SELECT → 데이터 없음 확인
        // 4. Replica SQL Thread 재시작 (START REPLICA SQL_THREAD)
        // 5. 잠시 대기 후 Replica에서 SELECT → 데이터 있음 확인
    }

    @Test
    @DisplayName("쓰기 후 즉시 읽기 시나리오에서 복제 지연 문제를 확인한다")
    void writeAndImmediateReadShowsInconsistency() {
        // TODO: 쓰기 후 즉시 읽기 문제 재현
        // 1. couponService.issue() → Source에 INSERT
        // 2. couponService.getMyCoupons() → Replica에서 SELECT
        // 3. 복제 지연 시 방금 발급한 쿠폰이 목록에 없을 수 있음을 확인
    }

    @Test
    @DisplayName("Seconds_Behind_Source로 복제 지연 시간을 모니터링한다")
    void monitorReplicationLagWithSecondsBehindSource() {
        // TODO: SHOW REPLICA STATUS에서 Seconds_Behind_Source 확인
        // 1. Source에 대량 INSERT 실행
        // 2. Replica에서 SHOW REPLICA STATUS 조회
        // 3. Seconds_Behind_Source > 0 확인
        // 4. 시간 경과 후 Seconds_Behind_Source가 0으로 돌아오는지 확인
    }
}
