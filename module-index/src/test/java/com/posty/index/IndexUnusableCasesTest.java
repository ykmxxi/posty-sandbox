package com.posty.index;

import com.posty.index.repository.CouponRepository;
import com.posty.index.util.ExplainAnalyzer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("인덱스 무효화 케이스 모음")
class IndexUnusableCasesTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExplainAnalyzer explainAnalyzer;

    @Test
    @DisplayName("컬럼에 함수를 적용하면 인덱스를 사용할 수 없다")
    void functionOnColumnDisablesIndex() {
        // TODO: WHERE SUBSTRING(name, 1, 1) = 'a' → EXPLAIN type이 ALL인지 확인
    }

    @Test
    @DisplayName("선행 와일드카드 LIKE는 인덱스를 사용할 수 없다")
    void leadingWildcardDisablesIndex() {
        // TODO: WHERE name LIKE '%apple' → EXPLAIN type이 ALL인지 확인
    }

    @Test
    @DisplayName("부정 조건(!=, NOT IN)은 인덱스 사용이 제한된다")
    void negationConditionLimitsIndex() {
        // TODO: WHERE status != 'EXPIRED' → EXPLAIN type 확인
    }

    @Test
    @DisplayName("컬럼에 산술 연산을 적용하면 인덱스를 사용할 수 없다")
    void arithmeticOnColumnDisablesIndex() {
        // TODO: WHERE discount_amount + 100 > 500 → EXPLAIN type이 ALL인지 확인
    }

    @Test
    @DisplayName("묵시적 타입 변환이 발생하면 인덱스를 사용할 수 없다")
    void implicitTypeCastDisablesIndex() {
        // TODO: 문자열 컬럼에 숫자로 비교 시 인덱스 무효화 확인
    }

    @Test
    @DisplayName("OR 조건에서 인덱스가 없는 컬럼이 포함되면 Full Table Scan이 발생한다")
    void orWithNonIndexedColumnCausesFullScan() {
        // TODO: WHERE indexed_col = ? OR non_indexed_col = ? → EXPLAIN type 확인
    }
}
