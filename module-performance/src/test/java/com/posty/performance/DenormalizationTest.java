package com.posty.performance;

import com.posty.performance.repository.PostRepository;
import com.posty.performance.repository.PostStatisticsRepository;
import com.posty.performance.service.DenormalizationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("역정규화: COUNT(*) vs 역정규화 테이블 성능 비교")
class DenormalizationTest extends BaseIntegrationTest {

    @Autowired
    private DenormalizationService denormalizationService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostStatisticsRepository postStatisticsRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("COUNT(*) 쿼리로 특정 유저의 게시글 수를 조회한다")
    void countPostsByCountQuery() {
        // TODO: 50만 건 데이터에서 COUNT(*) 쿼리 실행 시간 측정
    }

    @Test
    @DisplayName("역정규화 테이블에서 미리 집계된 게시글 수를 조회한다")
    void countPostsByDenormalizedTable() {
        // TODO: PostStatistics 테이블에서 단일 행 조회 실행 시간 측정
    }

    @Test
    @DisplayName("EXPLAIN ANALYZE로 두 방식의 스캔 행 수와 실행 시간을 비교한다")
    void compareWithExplainAnalyze() {
        // TODO: EXPLAIN ANALYZE로 COUNT(*) vs 역정규화 테이블 조회 실행 계획 비교
    }

    @Test
    @DisplayName("동기 방식으로 역정규화 테이블을 갱신하며 게시글을 생성한다")
    void syncDenormalizationUpdate() {
        // TODO: 게시글 INSERT + PostStatistics 갱신을 하나의 트랜잭션으로 처리
    }
}
