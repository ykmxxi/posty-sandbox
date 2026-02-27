package com.posty.performance;

import com.posty.performance.repository.CouponRepository;
import com.posty.performance.service.PagingService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("페이징 성능: Offset vs Cursor vs Deferred Join 비교")
class PagingPerformanceTest extends BaseIntegrationTest {

    @Autowired
    private PagingService pagingService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Offset 페이징: 페이지가 뒤로 갈수록 느려진다")
    void offsetPagingSlowsDown() {
        // TODO: OFFSET 0, 10000, 100000, 500000에서 각각 실행 시간 측정
    }

    @Test
    @DisplayName("Cursor 기반 페이징: 어느 페이지든 일정한 성능을 보인다")
    void cursorPagingConstantPerformance() {
        // TODO: 마지막 id 기반으로 동일한 위치에서 조회 시 실행 시간이 일정한지 확인
    }

    @Test
    @DisplayName("Deferred Join: 서브쿼리로 PK만 추출하여 Offset 성능을 개선한다")
    void deferredJoinImprovesOffset() {
        // TODO: 일반 OFFSET vs Deferred Join 패턴의 실행 시간 비교
    }

    @Test
    @DisplayName("EXPLAIN으로 Offset 페이징의 rows 값을 확인한다")
    void explainOffsetPaging() {
        // TODO: EXPLAIN으로 OFFSET 500000일 때 스캔하는 행 수 확인
    }

    @Test
    @DisplayName("세 가지 페이징 방식의 실행 시간을 정량적으로 비교한다")
    void compareAllPagingStrategies() {
        // TODO: 동일 위치(50만 번째)에서 Offset, Cursor, Deferred Join 실행 시간 비교
    }
}
