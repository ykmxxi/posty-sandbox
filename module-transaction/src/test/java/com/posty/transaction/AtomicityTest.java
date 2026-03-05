package com.posty.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("InnoDB vs MyISAM 원자성(Atomicity) 비교")
class AtomicityTest extends BaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("InnoDB 테이블에 중복 PK 삽입 시 전체 롤백된다")
    void innodb_duplicatePK_shouldRollbackAll() throws Exception {
        try (Connection connection = dataSource.getConnection(); // 데이터 소스에서 커넥션을 먼저 가져와서
             Statement statement = connection.createStatement() // 쿼리 실행을 위한 Statement 객체를 생성
        ) {
            log.info("test_innodb 테이블 생성");
            statement.execute("DROP TABLE IF EXISTS test_innodb");
            statement.execute("CREATE TABLE test_innodb (id INT PRIMARY KEY) ENGINE=InnoDB");

            log.info("test_innodb 테이블에 중복 PK 삽입 시 전체 롤백 확인");
            try {
                statement.execute("INSERT INTO test_innodb VALUES (1), (2), (3), (3), (4)");
            } catch (SQLException e) {
                log.info("중복 PK 삽입 시 전체 롤백 확인");
            }

            ResultSet resultSet = statement.executeQuery("SELECT * FROM test_innodb");
            List<Integer> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(resultSet.getInt("id"));
            }

            assertThat(ids).isEmpty();
        }
    }

    @Test
    @DisplayName("MyISAM 테이블에 중복 PK 삽입 시 에러 지점 이전까지만 삽입된다 (부분 삽입)")
    void myisam_duplicatePK_shouldPartialInsert() throws Exception {
        try (Connection connection = dataSource.getConnection(); // 데이터 소스에서 커넥션을 먼저 가져와서
             Statement statement = connection.createStatement() // 쿼리 실행을 위한 Statement 객체를 생성
        ) {
            log.info("test_innodb 테이블 생성");
            statement.execute("DROP TABLE IF EXISTS test_myisam");
            statement.execute("CREATE TABLE test_myisam (id INT PRIMARY KEY) ENGINE=MyISAM");

            log.info("test_innodb 테이블에 중복 PK 삽입 시 전체 롤백 확인");
            try {
                statement.execute("INSERT INTO test_myisam VALUES (1), (2), (3), (3), (4)");
            } catch (SQLException e) {
                log.info("MyISAM은 롤백 메커니즘이 없어 중복 PK 삽입 시 예외만 발생");
            }

            ResultSet resultSet = statement.executeQuery("SELECT * FROM test_myisam");
            List<Integer> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(resultSet.getInt("id"));
            }

            assertThat(ids).containsExactly(1, 2, 3);
        }
    }

    @Test
    @DisplayName("InnoDB에서 트랜잭션 중 예외 발생 시 전체 롤백된다")
    void innodb_exceptionInTransaction_shouldRollbackAll() throws Exception {
        try (
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
        ) {
            statement.execute("DROP TABLE IF EXISTS test_innodb");
            statement.execute("CREATE TABLE test_innodb (id INT PRIMARY KEY) ENGINE=InnoDB");

            log.info("트랜잭션 시작");
            connection.setAutoCommit(false);
            try {
                statement.execute("INSERT INTO test_innodb VALUES (1)");
                statement.execute("INSERT INTO test_innodb VALUES (2)");
                statement.execute("INSERT INTO test_innodb VALUES (3)");
                statement.execute("INSERT INTO test_innodb VALUES (2)"); // 중복 발생
            } catch (SQLException e) {
                log.info("InnoDB는 트랜잭션 내 예외 발생 시 전체 롤백");
                connection.rollback();
            }

            connection.setAutoCommit(true);
            log.info("트랜잭션 종료");

            List<Integer> ids = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM test_innodb");
            while (resultSet.next()) {
                ids.add(resultSet.getInt("id"));
            }

            assertThat(ids).isEmpty();
        }
    }
}
