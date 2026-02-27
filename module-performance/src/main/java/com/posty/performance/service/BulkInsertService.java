package com.posty.performance.service;

import com.posty.performance.domain.Coupon;
import com.posty.performance.repository.CouponRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BulkInsertService {

    private final CouponRepository couponRepository;
    private final JdbcTemplate jdbcTemplate;

    public BulkInsertService(CouponRepository couponRepository, JdbcTemplate jdbcTemplate) {
        this.couponRepository = couponRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * JPA saveAll로 대량 데이터를 삽입한다.
     * 엔티티 영속화 → dirty checking → 개별 INSERT 순서로 동작하여 느리다.
     * TODO: couponRepository.saveAll(coupons) 호출 후 소요 시간 측정
     */
    public void insertByJpaSaveAll(List<Coupon> coupons) {
        // TODO: JPA saveAll 대량 삽입 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }

    /**
     * JDBC Batch Insert로 대량 데이터를 삽입한다.
     * 다수의 INSERT를 하나의 네트워크 round-trip으로 처리하여 빠르다.
     * rewriteBatchedStatements=true 옵션이 JDBC URL에 필요하다.
     * TODO: jdbcTemplate.batchUpdate()로 Batch Insert 구현
     */
    public void insertByJdbcBatch(int count) {
        // TODO: JDBC Batch Insert 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }

    /**
     * MySQL LOAD DATA INFILE로 대량 데이터를 삽입한다.
     * MySQL이 직접 파일을 읽어 bulk load하며, 로깅과 인덱스 업데이트를 최소화한다.
     * TODO: CSV 파일 생성 후 LOAD DATA INFILE 네이티브 쿼리 실행
     */
    public void insertByLoadData(int count) {
        // TODO: LOAD DATA INFILE 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }
}
