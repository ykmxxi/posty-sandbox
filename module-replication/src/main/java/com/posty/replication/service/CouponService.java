package com.posty.replication.service;

import com.posty.replication.domain.Coupon;
import com.posty.replication.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * 쿠폰 발급 (쓰기 → Source로 라우팅).
     *
     * TODO: 쿠폰 발급 로직 구현
     * 1. 쿠폰 재고 확인
     * 2. CouponIssuance 생성
     * 3. 재고 차감
     */
    @Transactional
    public Coupon issue(String name, Integer quantity) {
        // TODO: 쿠폰 발급 구현
        return couponRepository.save(new Coupon(name, quantity, "ACTIVE"));
    }

    /**
     * 내 쿠폰 목록 조회 (읽기 → Replica로 라우팅).
     *
     * TODO: 사용자별 쿠폰 조회 로직 구현
     * - @Transactional(readOnly = true)에 의해 Replica DataSource로 라우팅된다
     * - 복제 지연 시 Source에서 방금 INSERT한 데이터가 보이지 않을 수 있다
     */
    @Transactional(readOnly = true)
    public List<Coupon> getMyCoupons() {
        // TODO: 사용자별 쿠폰 조회 구현
        return couponRepository.findAll();
    }
}
