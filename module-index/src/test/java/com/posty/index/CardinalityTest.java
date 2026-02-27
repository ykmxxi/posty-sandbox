package com.posty.index;

import com.posty.index.repository.CouponRepository;
import com.posty.index.util.ExplainAnalyzer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("카디널리티와 인덱스 선택")
class CardinalityTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExplainAnalyzer explainAnalyzer;

    @Test
    @DisplayName("카디널리티가 높은 컬럼의 인덱스는 조회 효율이 높다")
    void highCardinalityIndexIsEfficient() {
        // TODO: 카디널리티가 높은 컬럼(issueStartedAt 등) 인덱스 조회 시 rows 수 확인
    }

    @Test
    @DisplayName("카디널리티가 낮은 컬럼의 인덱스는 효과가 제한적이다")
    void lowCardinalityIndexIsLessEfficient() {
        // TODO: 카디널리티가 낮은 컬럼(status 등) 인덱스 조회 시 rows 수 확인
    }

    @Test
    @DisplayName("SHOW INDEX로 카디널리티를 확인한다")
    void checkCardinalityWithShowIndex() {
        // TODO: SHOW INDEX FROM coupon 실행 후 Cardinality 값 확인
    }

    @Test
    @DisplayName("ANALYZE TABLE 실행 후 카디널리티 통계가 갱신된다")
    void analyzeTableUpdatesStatistics() {
        // TODO: ANALYZE TABLE coupon 실행 전후 카디널리티 변화 관찰
    }
}
