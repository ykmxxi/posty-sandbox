package com.posty.index.util;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * EntityManager를 이용해 네이티브 EXPLAIN 쿼리를 실행하고 결과를 파싱하는 유틸리티.
 * 학습 시 직접 구현하며 EXPLAIN 출력 구조를 익힌다.
 */
@Component
public class ExplainAnalyzer {

    private final EntityManager entityManager;

    public ExplainAnalyzer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * EXPLAIN 실행 후 결과를 Map 리스트로 반환한다.
     * 각 Map은 EXPLAIN 결과의 한 행을 나타낸다.
     *
     * @param sql 분석할 SQL 쿼리 (SELECT 문)
     * @return EXPLAIN 결과 행 리스트
     */
    public List<Map<String, Object>> explain(String sql) {
        // TODO: 네이티브 쿼리로 "EXPLAIN " + sql 실행 후 결과를 Map으로 변환
        throw new UnsupportedOperationException("학습 시 직접 구현하세요");
    }

    /**
     * EXPLAIN 결과에서 type 컬럼 값을 추출한다.
     * (const, eq_ref, ref, range, index, ALL 등)
     *
     * @param sql 분석할 SQL 쿼리
     * @return 접근 타입 문자열
     */
    public String getAccessType(String sql) {
        // TODO: explain() 결과에서 "type" 키의 값을 추출
        throw new UnsupportedOperationException("학습 시 직접 구현하세요");
    }

    /**
     * EXPLAIN 결과에서 사용된 인덱스(key)를 추출한다.
     *
     * @param sql 분석할 SQL 쿼리
     * @return 사용된 인덱스 이름 (없으면 null)
     */
    public String getUsedIndex(String sql) {
        // TODO: explain() 결과에서 "key" 키의 값을 추출
        throw new UnsupportedOperationException("학습 시 직접 구현하세요");
    }

    /**
     * EXPLAIN 결과에서 Extra 컬럼 값을 추출한다.
     * (Using index, Using where, Using filesort, Using temporary 등)
     *
     * @param sql 분석할 SQL 쿼리
     * @return Extra 정보 문자열
     */
    public String getExtra(String sql) {
        // TODO: explain() 결과에서 "Extra" 키의 값을 추출
        throw new UnsupportedOperationException("학습 시 직접 구현하세요");
    }

    /**
     * EXPLAIN 결과에서 예상 스캔 행 수(rows)를 추출한다.
     *
     * @param sql 분석할 SQL 쿼리
     * @return 예상 스캔 행 수
     */
    public Long getEstimatedRows(String sql) {
        // TODO: explain() 결과에서 "rows" 키의 값을 추출
        throw new UnsupportedOperationException("학습 시 직접 구현하세요");
    }

    /**
     * EXPLAIN 결과를 보기 좋게 포맷팅하여 콘솔에 출력한다.
     *
     * @param sql 분석할 SQL 쿼리
     */
    public void printExplain(String sql) {
        // TODO: explain() 결과를 테이블 형태로 콘솔에 출력
        throw new UnsupportedOperationException("학습 시 직접 구현하세요");
    }
}
