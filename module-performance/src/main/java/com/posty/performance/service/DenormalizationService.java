package com.posty.performance.service;

import com.posty.performance.repository.PostRepository;
import com.posty.performance.repository.PostStatisticsRepository;
import org.springframework.stereotype.Service;

@Service
public class DenormalizationService {

    private final PostRepository postRepository;
    private final PostStatisticsRepository postStatisticsRepository;

    public DenormalizationService(PostRepository postRepository,
                                   PostStatisticsRepository postStatisticsRepository) {
        this.postRepository = postRepository;
        this.postStatisticsRepository = postStatisticsRepository;
    }

    /**
     * 정규화 방식: COUNT(*) 쿼리로 특정 유저의 게시글 수를 조회한다.
     * TODO: EntityManager를 사용하여 "SELECT COUNT(*) FROM post WHERE user_id = :userId" 네이티브 쿼리 실행
     */
    public long countPostsByUserId(Long userId) {
        // TODO: COUNT(*) 쿼리로 게시글 수 조회 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }

    /**
     * 역정규화 방식: PostStatistics 테이블에서 미리 집계된 값을 조회한다.
     * TODO: postStatisticsRepository.findByUserId()로 단일 행 조회
     */
    public int getPostCountFromStatistics(Long userId) {
        // TODO: 역정규화 테이블에서 게시글 수 조회 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }

    /**
     * 동기 방식으로 역정규화 테이블을 갱신한다.
     * 게시글 INSERT 시 같은 트랜잭션에서 post_count를 증가시킨다.
     * TODO: Post 저장 + PostStatistics.postCount += 1 을 하나의 트랜잭션으로 처리
     */
    public void createPostWithSyncUpdate(Long userId, String title, String content) {
        // TODO: 동기 역정규화 갱신 구현
        throw new UnsupportedOperationException("TODO: 구현 필요");
    }
}
