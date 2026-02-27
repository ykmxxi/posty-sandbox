package com.posty.index;

import com.posty.index.repository.FruitRepository;
import com.posty.index.util.ExplainAnalyzer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("커버링 인덱스 효과")
class CoveringIndexTest extends BaseIntegrationTest {

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExplainAnalyzer explainAnalyzer;

    @Test
    @DisplayName("커버링 인덱스 적용 시 Extra에 Using index가 표시된다")
    void coveringIndexShowsUsingIndex() {
        // TODO: SELECT name FROM fruit WHERE name LIKE 'ap%' → Extra에 Using index 확인
    }

    @Test
    @DisplayName("SELECT *는 커버링 인덱스가 적용되지 않아 테이블 룩업이 발생한다")
    void selectAllDoesNotUseCoveringIndex() {
        // TODO: SELECT * FROM fruit WHERE name LIKE 'ap%' → Extra에 Using index 없는 것 확인
    }

    @Test
    @DisplayName("커버링 인덱스와 비커버링 인덱스의 스캔 행 수를 비교한다")
    void compareScannedRows() {
        // TODO: 동일 조건에서 커버링 vs 비커버링 쿼리의 rows 비교
    }

    @Test
    @DisplayName("커버링 인덱스와 비커버링 인덱스의 실행 시간을 비교한다")
    void compareExecutionTime() {
        // TODO: EXPLAIN ANALYZE로 실행 시간 차이 측정
    }
}
