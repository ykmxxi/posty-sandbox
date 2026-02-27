package com.posty.transaction.service;

import com.posty.transaction.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Service
@Transactional
public class AtomicityDemoService {

    private final CouponRepository couponRepository;
    private final DataSource dataSource;

    public AtomicityDemoService(CouponRepository couponRepository, DataSource dataSource) {
        this.couponRepository = couponRepository;
        this.dataSource = dataSource;
    }

    /**
     * InnoDB 테이블에 중복 PK 삽입 시 전체 롤백 확인.
     * TODO: 네이티브 쿼리로 InnoDB 테이블 생성 후 중복 PK INSERT → 전체 롤백 확인
     */
    public void demonstrateInnoDBAtomicity() {
        // TODO: InnoDB 원자성 실습 구현
    }

    /**
     * MyISAM 테이블에 중복 PK 삽입 시 부분 삽입(partial insert) 확인.
     * MyISAM은 트랜잭션을 지원하지 않으므로 에러 지점 이전까지만 삽입된다.
     * TODO: 네이티브 쿼리로 MyISAM 테이블 생성 후 중복 PK INSERT → 부분 삽입 확인
     */
    public void demonstrateMyISAMPartialInsert() {
        // TODO: MyISAM 부분 삽입 실습 구현
    }
}
