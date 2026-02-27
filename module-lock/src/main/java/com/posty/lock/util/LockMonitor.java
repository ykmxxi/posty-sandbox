package com.posty.lock.util;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LockMonitor {

    private final EntityManager entityManager;

    public LockMonitor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * performance_schema.data_locks에서 현재 걸린 모든 락을 조회한다.
     *
     * TODO: 네이티브 쿼리로 data_locks 조회 구현
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getCurrentLocks() {
        // TODO: 구현
        // SELECT ENGINE_TRANSACTION_ID, OBJECT_NAME, INDEX_NAME,
        //        LOCK_TYPE, LOCK_MODE, LOCK_STATUS, LOCK_DATA
        // FROM performance_schema.data_locks
        // ORDER BY ENGINE_TRANSACTION_ID
        return List.of();
    }

    /**
     * 락 대기 관계(누가 누구를 기다리는지)를 조회한다.
     *
     * TODO: INNODB_TRX + data_lock_waits 조인 쿼리 구현
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getLockWaitRelations() {
        // TODO: 구현
        // SELECT r.trx_id, r.trx_mysql_thread_id,
        //        b.trx_id, b.trx_mysql_thread_id, r.trx_query
        // FROM information_schema.INNODB_TRX r
        // JOIN performance_schema.data_lock_waits w
        //   ON r.trx_id = w.REQUESTING_ENGINE_TRANSACTION_ID
        // JOIN information_schema.INNODB_TRX b
        //   ON b.trx_id = w.BLOCKING_ENGINE_TRANSACTION_ID
        return List.of();
    }

    /**
     * 현재 락 상태를 콘솔에 보기 좋게 출력한다.
     *
     * TODO: getCurrentLocks() 결과를 포맷팅하여 출력
     */
    public void printCurrentLocks() {
        // TODO: 구현
    }
}
