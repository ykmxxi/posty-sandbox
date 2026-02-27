package com.posty.lock.service;

import com.posty.lock.repository.NamedLockRepository;
import org.springframework.stereotype.Service;

@Service
public class NamedLockService {

    private final NamedLockRepository namedLockRepository;

    public NamedLockService(NamedLockRepository namedLockRepository) {
        this.namedLockRepository = namedLockRepository;
    }

    /**
     * MySQL User-Level Lock(GET_LOCK / RELEASE_LOCK)을 사용한 동시성 제어.
     *
     * Named Lock은 트랜잭션과 독립적으로 동작하므로,
     * 비즈니스 로직의 트랜잭션 커넥션과 Named Lock 커넥션을 분리해야 한다.
     *
     * TODO: Named Lock 기반 쿠폰 발급 직렬화 구현
     */
    public void executeWithLock(String lockName, int timeout, Runnable action) {
        // TODO: Named Lock 기반 동시성 제어 구현
        // 1. namedLockRepository.getLock(lockName, timeout) → GET_LOCK 호출
        // 2. try { action.run(); } → 비즈니스 로직 실행 (별도 트랜잭션)
        // 3. finally { namedLockRepository.releaseLock(lockName); } → RELEASE_LOCK 호출
    }
}
