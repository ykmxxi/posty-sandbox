package com.posty.index;

import com.posty.index.repository.CouponRepository;
import com.posty.index.util.ExplainAnalyzer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Clustered Index vs Secondary Index 조회 비교")
class ClusteredIndexTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExplainAnalyzer explainAnalyzer;

    @Test
    @DisplayName("PK 조회 시 const 타입으로 접근한다")
    void pkLookupUsesConst() {
        // TODO: WHERE id = ? 쿼리의 EXPLAIN type이 const인지 확인
    }

    @Test
    @DisplayName("Secondary Index 조회 시 ref 또는 range 타입으로 접근한다")
    void secondaryIndexUsesRefOrRange() {
        // TODO: Secondary Index 컬럼 조회 시 EXPLAIN type 확인
    }

    @Test
    @DisplayName("Secondary Index 조회 시 테이블 룩업이 발생한다")
    void secondaryIndexRequiresTableLookup() {
        // TODO: SELECT * 시 Extra에 Using index가 없는 것을 확인 (테이블 룩업 발생)
    }

    @Test
    @DisplayName("PK 범위 조회에서 옵티마이저가 인덱스 사용 여부를 판단한다")
    void optimizerDecisionOnPkRange() {
        // TODO: 좁은 범위(id < 10005) vs 넓은 범위(id >= 999000) 비교
        //       옵티마이저가 조회 비율에 따라 인덱스 사용 여부를 다르게 결정하는지 확인
    }
}
