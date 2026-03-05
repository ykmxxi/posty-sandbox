package com.posty.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("READ UNCOMMITTED에서 Dirty Read 재현")
class DirtyReadTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.execute("DELETE FROM coupon");
            statement.execute("INSERT INTO coupon (id, name, discount_amount) VALUES (1, 'Alice', 1000)");
        }
    }

    @Test
    @DisplayName("READ UNCOMMITTED에서 커밋되지 않은 데이터를 읽을 수 있다 (Dirty Read)")
    void readUncommitted_shouldSeeDirtyData() throws Exception {
        // 세션 A, 세션 B 각각 독립된 커넥션을 생성한다
        try (Connection sessionA = dataSource.getConnection();
             Connection sessionB = dataSource.getConnection();
             Statement stmtA = sessionA.createStatement();
             Statement stmtB = sessionB.createStatement()
        ) {
            // 세션 A: 격리 수준을 READ UNCOMMITTED로 설정 (커밋되지 않은 데이터도 읽을 수 있는 가장 낮은 격리 수준)
            sessionA.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            // 세션 A: 자동 커밋을 끄고 트랜잭션을 수동 제어한다
            sessionA.setAutoCommit(false);

            // 세션 B: 자동 커밋을 끄고 트랜잭션을 시작한다
            sessionB.setAutoCommit(false);
            // 세션 B: discount_amount를 1000 → 9999로 변경하되, 커밋하지 않는다
            stmtB.executeUpdate("UPDATE coupon SET discount_amount = 9999 WHERE id = 1");

            // 세션 A: 세션 B가 아직 커밋하지 않은 상태에서 SELECT를 수행한다
            ResultSet rs1 = stmtA.executeQuery("SELECT discount_amount FROM coupon WHERE id = 1");
            rs1.next();
            int dirtyValue = rs1.getInt("discount_amount");

            // 세션 B가 커밋하지 않은 9999가 보인다 → 이것이 Dirty Read이다
            assertThat(dirtyValue).isEqualTo(9999);

            // 세션 B: 변경을 롤백한다 (결국 커밋되지 않은 데이터였다)
            sessionB.rollback();

            // 세션 A: 롤백 후 다시 SELECT를 수행한다
            ResultSet rs2 = stmtA.executeQuery("SELECT discount_amount FROM coupon WHERE id = 1");
            rs2.next();
            int rolledBackValue = rs2.getInt("discount_amount");

            // 세션 B가 롤백했으므로 원래 값 1000으로 돌아왔다
            assertThat(rolledBackValue).isEqualTo(1000);

            // 세션 A: 트랜잭션을 커밋하여 정리한다
            sessionA.commit();
        }
    }

    @Test
    @DisplayName("READ COMMITTED에서는 커밋되지 않은 데이터를 읽을 수 없다")
    void readCommitted_shouldNotSeeDirtyData() throws Exception {
        // TODO: 동일 시나리오에서 READ COMMITTED로 변경 → Dirty Read 발생하지 않음 확인
        try (Connection sessionA = dataSource.getConnection();
             Connection sessionB = dataSource.getConnection();
             Statement stmtA = sessionA.createStatement();
             Statement stmtB = sessionB.createStatement()
        ) {
            // 세션 A: 격리 수준을 READ UNCOMMITTED로 설정 (커밋되지 않은 데이터도 읽을 수 있는 가장 낮은 격리 수준)
            sessionA.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // 세션 A: 자동 커밋을 끄고 트랜잭션을 수동 제어한다
            sessionA.setAutoCommit(false);

            // 세션 B: 자동 커밋을 끄고 트랜잭션을 시작한다
            sessionB.setAutoCommit(false);
            // 세션 B: discount_amount를 1000 → 9999로 변경하되, 커밋하지 않는다
            stmtB.executeUpdate("UPDATE coupon SET discount_amount = 9999 WHERE id = 1");

            // 세션 A: 세션 B가 아직 커밋하지 않은 상태에서 SELECT를 수행한다
            ResultSet rs1 = stmtA.executeQuery("SELECT discount_amount FROM coupon WHERE id = 1");
            rs1.next();
            int dirtyValue = rs1.getInt("discount_amount");

            // READ COMMITTED 수준에서는 세션 B가 커밋하지 않은 값은 보이지 않는다.
            // 오직 커밋된 값만 읽어온다. (세션 A가 SELECT 할때마다 새로운 read view 생성)
            assertThat(dirtyValue).isEqualTo(1000);

            // 세션 B: 변경을 커밋한다.
            sessionB.commit();

            // 세션 A: 커밋 후 다시 SELECT를 수행한다
            ResultSet rs2 = stmtA.executeQuery("SELECT discount_amount FROM coupon WHERE id = 1");
            rs2.next();
            int committedValue = rs2.getInt("discount_amount");

            // 세션 B가 커밋해 새로운 값 9999로 변경된다
            assertThat(committedValue).isEqualTo(9999);

            // 세션 A: 트랜잭션을 커밋하여 정리한다
            sessionA.commit();
        }
    }
}
