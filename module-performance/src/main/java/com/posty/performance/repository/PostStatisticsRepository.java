package com.posty.performance.repository;

import com.posty.performance.domain.PostStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostStatisticsRepository extends JpaRepository<PostStatistics, Long> {

    Optional<PostStatistics> findByUserId(Long userId);
}
