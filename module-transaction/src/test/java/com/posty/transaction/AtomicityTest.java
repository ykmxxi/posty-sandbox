package com.posty.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

@DisplayName("InnoDB vs MyISAM 원자성(Atomicity) 비교")
class AtomicityTest extends BaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("InnoDB 테이블에 중복 PK 삽입 시 전체 롤백된다")
    void innodb_duplicatePK_shouldRollbackAll() {
        // TODO: InnoDB 테이블 생성 → 중복 PK INSERT → 전체 롤백 확인
    }

    @Test
    @DisplayName("MyISAM 테이블에 중복 PK 삽입 시 에러 지점 이전까지만 삽입된다 (부분 삽입)")
    void myisam_duplicatePK_shouldPartialInsert() {
        // TODO: MyISAM 테이블 생성 → 중복 PK INSERT → 부분 삽입 확인
    }

    @Test
    @DisplayName("InnoDB에서 트랜잭션 중 예외 발생 시 전체 롤백된다")
    void innodb_exceptionInTransaction_shouldRollbackAll() {
        // TODO: 트랜잭션 내 여러 INSERT 후 예외 발생 → 전체 롤백 확인
    }
}
