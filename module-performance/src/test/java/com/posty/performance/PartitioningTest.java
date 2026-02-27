package com.posty.performance;

import com.posty.performance.repository.UserOrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@DisplayName("파티셔닝: RANGE 파티셔닝 효과 확인")
class PartitioningTest extends BaseIntegrationTest {

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("RANGE 파티셔닝 테이블을 생성하고 데이터를 삽입한다")
    void createPartitionedTableAndInsertData() {
        // TODO: @Sql로 파티셔닝 DDL 실행 후 대량 데이터 삽입
    }

    @Test
    @DisplayName("파티션 프루닝: 특정 월 범위 쿼리 시 해당 파티션만 접근한다")
    void partitionPruning() {
        // TODO: EXPLAIN으로 partitions 컬럼에서 특정 파티션만 접근하는지 확인
    }

    @Test
    @DisplayName("파티션 키가 아닌 컬럼으로 조회하면 모든 파티션을 스캔한다")
    void fullPartitionScanWithoutPartitionKey() {
        // TODO: 파티션 키가 아닌 user_id로 조회 시 모든 파티션 스캔 확인
    }

    @Test
    @DisplayName("파티셔닝 테이블 vs 일반 테이블의 범위 쿼리 성능을 비교한다")
    void comparePartitionedVsNonPartitioned() {
        // TODO: 동일 데이터에 대해 파티셔닝 테이블과 일반 테이블의 범위 쿼리 실행 시간 비교
    }
}
