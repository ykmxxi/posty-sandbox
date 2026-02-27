package com.posty.replication;

import com.posty.replication.repository.CouponRepository;
import com.posty.replication.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

@DisplayName("DataSource 라우팅 테스트")
class RoutingDataSourceTest extends BaseIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("@Transactional(readOnly = true) 시 Replica DataSource로 라우팅된다")
    void readOnlyTransactionRoutesToReplica() {
        // TODO: readOnly=true 트랜잭션에서 Replica로 라우팅되는지 확인
        // 1. couponService.getMyCoupons() 호출 (readOnly = true)
        // 2. p6spy 로그에서 Replica(3308) 커넥션으로 쿼리가 실행되었는지 확인
    }

    @Test
    @DisplayName("@Transactional (기본값) 시 Source DataSource로 라우팅된다")
    void writeTransactionRoutesToSource() {
        // TODO: 기본 트랜잭션에서 Source로 라우팅되는지 확인
        // 1. couponService.issue() 호출 (readOnly = false, 기본값)
        // 2. p6spy 로그에서 Source(3307) 커넥션으로 쿼리가 실행되었는지 확인
    }

    @Test
    @DisplayName("LazyConnectionDataSourceProxy가 실제 쿼리 실행 시점까지 커넥션 획득을 지연한다")
    void lazyConnectionDelaysConnectionAcquisition() {
        // TODO: LazyConnectionDataSourceProxy 동작 확인
        // 1. 트랜잭션 시작 시점에는 커넥션을 확보하지 않음
        // 2. 실제 쿼리 실행 시점에 readOnly 여부를 판단하여 적절한 DataSource에서 커넥션 획득
    }
}
