package com.posty.performance;

import com.posty.performance.repository.CouponRepository;
import com.posty.performance.service.BulkInsertService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@DisplayName("대량 삽입: JPA saveAll vs JDBC Batch vs LOAD DATA 성능 비교")
class BulkInsertTest extends BaseIntegrationTest {

    @Autowired
    private BulkInsertService bulkInsertService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("JPA saveAll로 대량 데이터를 삽입한다")
    void insertByJpaSaveAll() {
        // TODO: JPA saveAll로 10만 건 삽입 후 소요 시간 측정
    }

    @Test
    @DisplayName("JDBC Batch Insert로 대량 데이터를 삽입한다")
    void insertByJdbcBatch() {
        // TODO: JDBC Batch Insert로 10만 건 삽입 후 소요 시간 측정
    }

    @Test
    @DisplayName("LOAD DATA INFILE로 대량 데이터를 삽입한다")
    void insertByLoadData() {
        // TODO: LOAD DATA INFILE로 10만 건 삽입 후 소요 시간 측정
    }

    @Test
    @DisplayName("세 가지 삽입 방식의 소요 시간을 정량적으로 비교한다")
    void compareAllInsertStrategies() {
        // TODO: JPA saveAll, JDBC Batch, LOAD DATA의 삽입 시간을 측정하고 비교
    }
}
