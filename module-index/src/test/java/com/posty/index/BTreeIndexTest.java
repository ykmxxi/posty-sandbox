package com.posty.index;

import com.posty.index.repository.CouponRepository;
import com.posty.index.util.ExplainAnalyzer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("B+Tree 인덱스 동작 확인")
class BTreeIndexTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExplainAnalyzer explainAnalyzer;

    @Test
    @DisplayName("인덱스가 없을 때 Full Table Scan이 발생한다")
    void fullTableScanWithoutIndex() {
        // TODO: 인덱스 없는 컬럼으로 조회 시 EXPLAIN type이 ALL인지 확인
    }

    @Test
    @DisplayName("인덱스가 있으면 range 또는 ref 스캔으로 변경된다")
    void indexScanWithIndex() {
        // TODO: 인덱스 생성 후 동일 쿼리의 EXPLAIN type이 range/ref로 변경되는지 확인
    }

    @Test
    @DisplayName("B+Tree 인덱스는 정렬된 상태를 유지한다")
    void bTreeMaintainsSortedOrder() {
        // TODO: 인덱스 컬럼 기준 ORDER BY 시 filesort 없이 정렬되는지 확인
    }

    @Test
    @DisplayName("인덱스를 사용하면 스캔 행 수가 크게 줄어든다")
    void indexReducesScannedRows() {
        // TODO: 동일 쿼리에 대해 인덱스 유무별 rows 비교
    }
}
