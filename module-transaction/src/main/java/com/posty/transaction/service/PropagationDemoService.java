package com.posty.transaction.service;

import com.posty.transaction.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationDemoService {

    private final CouponRepository couponRepository;

    public PropagationDemoService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * REQUIRED(기본값) 전파 속성: 기존 트랜잭션이 있으면 참여한다.
     * TODO: 기존 트랜잭션 참여 동작 확인
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void requiredMethod() {
        // TODO: REQUIRED 전파 속성 실습 구현
    }

    /**
     * REQUIRES_NEW 전파 속성: 새 커넥션을 획득해서 별도 트랜잭션을 실행한다.
     * ⚠️ 주의: 외부 트랜잭션이 커넥션 1개를 점유한 상태에서
     * 내부 REQUIRES_NEW가 커넥션 1개를 추가로 요청하면,
     * HikariCP pool size가 작을 경우 데드락이 발생할 수 있다.
     * TODO: REQUIRES_NEW 커넥션 풀 고갈 시나리오 구현
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNewMethod() {
        // TODO: REQUIRES_NEW 전파 속성 실습 구현
    }
}
