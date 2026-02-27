package com.posty.index;

import com.posty.index.repository.CouponRepository;
import com.posty.index.util.ExplainAnalyzer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("복합 인덱스 컬럼 순서 실험")
class CompositeIndexTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExplainAnalyzer explainAnalyzer;

    @Test
    @DisplayName("복합 인덱스의 첫 번째 컬럼으로 조회하면 인덱스를 사용한다")
    void firstColumnUsesIndex() {
        // TODO: (status, issue_started_at) 복합 인덱스에서 WHERE status = ? 조회 시 인덱스 사용 확인
    }

    @Test
    @DisplayName("복합 인덱스의 두 번째 컬럼만으로 조회하면 인덱스를 사용하지 못한다")
    void secondColumnOnlyDoesNotUseIndex() {
        // TODO: WHERE issue_started_at > ? 만으로 조회 시 인덱스 미사용 확인 (최좌선 접두사 규칙)
    }

    @Test
    @DisplayName("복합 인덱스의 모든 컬럼을 사용하면 가장 효율적이다")
    void allColumnsUsesIndexEfficiently() {
        // TODO: WHERE status = ? AND issue_started_at > ? 조회 시 인덱스 사용 및 rows 비교
    }

    @Test
    @DisplayName("등호 조건을 앞에 두고 범위 조건을 뒤에 두는 것이 효율적이다")
    void equalityBeforeRangeIsEfficient() {
        // TODO: (status, issue_started_at) vs (issue_started_at, status) 인덱스 효율 비교
    }
}
