package posty.jpa.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import posty.jpa.domain.example.Post;
import posty.jpa.domain.example.User;
import posty.jpa.repository.PostRepository;
import posty.jpa.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("user1", "user1@email.com"));
        user2 = userRepository.save(new User("user2", "user2@email.com"));

        postRepository.save(new Post("content1", user1));
        postRepository.save(new Post("content2", user1));
        postRepository.save(new Post("content3", user1));
        postRepository.save(new Post("content4", user2));

    }

    @DisplayName("지연로딩 테스트")
    @Test
    void lazyLoading() {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
            .unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true); // Hibernate 수집 활성화
        stats.clear();

        List<User> users = userService.findAll(); // 1

        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
    }

    @DisplayName("N + 1 발생: 1 + 1 요청에서 N + 1 문제 발생 상황")
    @Test
    void nPlusOneProblem() {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
            .unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true); // Hibernate 수집 활성화
        stats.clear();

        int numberOfRequests = userService.count();

        System.out.println("PrepareStatementCount: " + stats.getPrepareStatementCount());
        System.out.println("QueryExecutionCount: " + stats.getQueryExecutionCount());
        System.out.println("EntityLoadCount: " + stats.getEntityLoadCount());

        // yml에서 batch_fetch 설정을 끄면 3개의 쿼리가 나감 (1개의 user 조회 + 2개(유저 수) post 조회 쿼리)
        assertThat(stats.getPrepareStatementCount()).isEqualTo(2);
    }

}
