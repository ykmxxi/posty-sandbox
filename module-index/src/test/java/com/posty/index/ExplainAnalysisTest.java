package com.posty.index;

import com.posty.index.repository.CouponRepository;
import com.posty.index.util.ExplainAnalyzer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("EXPLAIN type별 실행 계획 분석")
class ExplainAnalysisTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExplainAnalyzer explainAnalyzer;

    @Test
    @DisplayName("type=const: PK 또는 UNIQUE 인덱스로 단일 행 조회")
    void typeConst() {
        // TODO: WHERE id = ? 쿼리로 const 타입 확인
    }

    @Test
    @DisplayName("type=ref: 비고유 인덱스로 동등 조건 조회")
    void typeRef() {
        // TODO: 비고유 인덱스 컬럼의 WHERE col = ? 쿼리로 ref 타입 확인
    }

    @Test
    @DisplayName("type=range: 인덱스 범위 스캔")
    void typeRange() {
        // TODO: WHERE col > ? AND col < ? 쿼리로 range 타입 확인
    }

    @Test
    @DisplayName("type=index: 인덱스 풀 스캔 (Full Index Scan)")
    void typeIndex() {
        // TODO: 인덱스 컬럼만 SELECT하는 쿼리로 index 타입 확인
    }

    @Test
    @DisplayName("type=ALL: 풀 테이블 스캔")
    void typeAll() {
        // TODO: 인덱스 없는 컬럼 조회로 ALL 타입 확인
    }

    @Test
    @DisplayName("Extra 필드의 Using index, Using where, Using filesort 의미 비교")
    void extraFieldComparison() {
        // TODO: 각 Extra 값이 나오는 쿼리를 실행하고 의미를 비교
    }
}
