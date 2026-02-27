package com.posty.transaction;

import com.posty.transaction.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("REPEATABLE READ에서 Phantom Read 재현 및 InnoDB 방어 확인")
class PhantomReadTest extends BaseIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("REPEATABLE READ에서 다른 트랜잭션이 INSERT해도 기존 스냅샷이 유지된다 (MVCC)")
    void repeatableRead_shouldPreventPhantomRead() {
        // TODO: InnoDB의 MVCC가 Phantom Read를 방지하는지 확인
        // 1. 초기 데이터 삽입 (status = 'ACTIVE' 5건)
        // 2. 세션 A: REPEATABLE READ 트랜잭션 시작 → SELECT WHERE status = 'ACTIVE' (5건)
        // 3. 세션 B: INSERT (status = 'ACTIVE') → COMMIT
        // 4. 세션 A: 같은 SELECT → 여전히 5건 (MVCC 스냅샷)
    }

    @Test
    @DisplayName("READ COMMITTED에서는 다른 트랜잭션의 INSERT가 보인다 (Phantom Read)")
    void readCommitted_shouldAllowPhantomRead() {
        // TODO: READ COMMITTED에서 Phantom Read 발생 확인
        // 1. 초기 데이터 삽입 (status = 'ACTIVE' 5건)
        // 2. 세션 A: READ COMMITTED 트랜잭션 시작 → SELECT WHERE status = 'ACTIVE' (5건)
        // 3. 세션 B: INSERT (status = 'ACTIVE') → COMMIT
        // 4. 세션 A: 같은 SELECT → 6건 (Phantom Read 발생)
    }

    @Test
    @DisplayName("REPEATABLE READ에서 SELECT ... FOR UPDATE는 현재 시점의 실제 데이터를 읽는다 (Locking Read)")
    void repeatableRead_lockingRead_shouldSeeCurrentData() {
        // TODO: Locking Read는 MVCC 스냅샷이 아닌 최신 데이터를 읽는다
    }
}
