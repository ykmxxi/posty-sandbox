package com.posty.transaction.service;

import com.posty.transaction.repository.CouponRepository;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class IsolationDemoService {

    private final CouponRepository couponRepository;
    private final DataSource dataSource;

    public IsolationDemoService(CouponRepository couponRepository, DataSource dataSource) {
        this.couponRepository = couponRepository;
        this.dataSource = dataSource;
    }

    /**
     * READ UNCOMMITTED 격리 수준에서 Dirty Read 재현.
     * 세션 B가 커밋하지 않은 데이터를 세션 A가 읽을 수 있다.
     * TODO: 두 커넥션으로 Dirty Read 시나리오 구현
     */
    public void demonstrateDirtyRead() {
        // TODO: Dirty Read 재현 구현
    }

    /**
     * READ COMMITTED 격리 수준에서 Non-Repeatable Read 재현.
     * 같은 트랜잭션 내에서 같은 SELECT가 다른 결과를 반환한다.
     * TODO: 두 커넥션으로 Non-Repeatable Read 시나리오 구현
     */
    public void demonstrateNonRepeatableRead() {
        // TODO: Non-Repeatable Read 재현 구현
    }

    /**
     * REPEATABLE READ 격리 수준에서 Phantom Read 테스트.
     * InnoDB는 MVCC + Next-Key Lock으로 대부분의 Phantom Read를 방지한다.
     * TODO: 두 커넥션으로 Phantom Read 방지 확인
     */
    public void demonstratePhantomRead() {
        // TODO: Phantom Read 시나리오 구현
    }

    /**
     * SERIALIZABLE 격리 수준 동작 확인.
     * 일반 SELECT도 LOCK IN SHARE MODE처럼 동작한다.
     * TODO: SERIALIZABLE에서의 동시 접근 시나리오 구현
     */
    public void demonstrateSerializable() {
        // TODO: SERIALIZABLE 동작 확인 구현
    }
}
