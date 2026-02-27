# module-transaction: 트랜잭션 학습

> **핵심 질문**: 동시에 실행되는 트랜잭션은 서로를 어떻게 격리하는가?

## 학습 목표

이 모듈을 마치면 다음을 설명할 수 있어야 한다:

- ACID 각 속성이 실제로 어떤 보장을 하는지 (특히 InnoDB vs MyISAM의 원자성 차이)
- 4가지 격리 수준에서 발생할 수 있는 읽기 부정합 현상을 직접 재현하고 설명
- InnoDB의 MVCC(Multi-Version Concurrency Control)가 읽기-쓰기 충돌을 회피하는 원리
- Spring `@Transactional`이 실제 MySQL 세션에 어떤 명령을 내리는지
- 트랜잭션 전파(propagation)가 커넥션 풀에 미치는 영향

## 선행 지식

- **module-index 완료**: 인덱스 구조를 알아야 "어디에 락이 걸리는지"를 이해할 수 있다
- Spring Boot + JPA 기본 사용법

---

## 학습 로드맵

### Step 1: ACID 속성 실체 확인

- [ ] **원자성(Atomicity)** — InnoDB vs MyISAM 비교

```sql
-- InnoDB 테이블: 중복 PK 삽입 시 전체 롤백
CREATE TABLE test_innodb (id INT PRIMARY KEY) ENGINE=InnoDB;
INSERT INTO test_innodb VALUES (1), (2), (3), (3), (4);  -- 에러 발생
SELECT * FROM test_innodb;  -- 결과: 비어있음 (전체 롤백)

-- MyISAM 테이블: 에러 지점 이전까지만 삽입 (부분 삽입)
CREATE TABLE test_myisam (id INT PRIMARY KEY) ENGINE=MyISAM;
INSERT INTO test_myisam VALUES (1), (2), (3), (3), (4);  -- 에러 발생
SELECT * FROM test_myisam;  -- 결과: 1, 2, 3 (부분 삽입!)
```

- [ ] 이 차이가 발생하는 이유: InnoDB는 undo log를 통한 롤백을 지원하지만, MyISAM은 트랜잭션을 지원하지 않는다
- [ ] **내구성(Durability)**: `innodb_flush_log_at_trx_commit` 설정값(0, 1, 2)에 따른 redo log flush 전략 차이 이해 (개념만, 실습은 선택)

### Step 2: 격리 수준과 읽기 부정합 재현

여기가 이 모듈의 핵심이다. **2개의 MySQL 세션(커넥션)**을 동시에 열어서 실험한다.

#### 2-1. Dirty Read 재현 (READ UNCOMMITTED)

- [ ] 세션 A: `SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;`
- [ ] 세션 B: 트랜잭션 시작 → 데이터 UPDATE → **커밋하지 않음**
- [ ] 세션 A: SELECT → 커밋되지 않은 데이터가 보인다 (Dirty Read)
- [ ] 세션 B: ROLLBACK
- [ ] 세션 A: SELECT → 데이터가 사라졌다 → 존재하지 않는 데이터를 읽은 셈

**핵심 포인트**: 실무에서 READ UNCOMMITTED를 쓸 일은 거의 없다. 하지만 Dirty Read를 이해해야 다른 격리 수준이 "무엇을 방지하는지"가 명확해진다.

#### 2-2. Non-Repeatable Read 재현 (READ COMMITTED)

- [ ] 세션 A: `READ COMMITTED`로 트랜잭션 시작 → SELECT (결과 X)
- [ ] 세션 B: 같은 행 UPDATE → COMMIT
- [ ] 세션 A: 같은 SELECT 다시 실행 → 결과가 달라짐 (Non-Repeatable Read)

**핵심 포인트**: READ COMMITTED에서는 매 SELECT마다 새로운 스냅샷을 읽는다. 같은 트랜잭션 안에서도 다른 트랜잭션의 커밋 결과가 보인다.

#### 2-3. REPEATABLE READ에서의 일관된 읽기 확인

- [ ] 세션 A: `REPEATABLE READ`로 트랜잭션 시작 → SELECT (결과 X)
- [ ] 세션 B: 같은 행 UPDATE → COMMIT
- [ ] 세션 A: 같은 SELECT → **여전히 결과 X** (MVCC 스냅샷)
- [ ] 세션 A: COMMIT 후 다시 SELECT → 이제 변경된 결과가 보임

**핵심 포인트**: REPEATABLE READ는 트랜잭션 시작 시점의 스냅샷을 계속 읽는다. 이것이 MVCC의 핵심이다.

#### 2-4. Phantom Read와 InnoDB의 방어

- [ ] 세션 A: `REPEATABLE READ`에서 `SELECT * FROM coupon WHERE status = 'ACTIVE'` (결과 5건)
- [ ] 세션 B: `INSERT INTO coupon (status) VALUES ('ACTIVE')` → COMMIT
- [ ] 세션 A: 같은 SELECT → **여전히 5건** (InnoDB의 MVCC가 Phantom Read 방지)
- [ ] 단, `SELECT ... FOR UPDATE`는 현재 시점의 실제 데이터를 읽는다 (Locking Read) → 6건이 보일 수 있다

**핵심 포인트**: 표준 SQL에서 REPEATABLE READ는 Phantom Read를 허용하지만, **InnoDB는 MVCC + Next-Key Lock으로 대부분의 Phantom Read를 방지**한다. 이 "대부분"의 예외 케이스를 module-lock에서 다룬다.

#### 2-5. SERIALIZABLE 동작 확인

- [ ] SERIALIZABLE에서는 일반 SELECT도 `SELECT ... LOCK IN SHARE MODE`처럼 동작한다
- [ ] 두 세션이 같은 범위를 읽으면 → 한쪽이 UPDATE를 시도할 때 데드락 또는 대기 발생

### Step 3: MVCC 내부 동작 이해 (개념)

- [ ] **Undo Log**: 행의 이전 버전을 보관. MVCC 읽기 시 트랜잭션 ID를 비교해 자신의 스냅샷에 맞는 버전을 찾아간다
- [ ] **Read View**: 트랜잭션이 시작될 때 생성되는 "보이는 트랜잭션 목록"
  - REPEATABLE READ: 트랜잭션 최초 SELECT 시 Read View 생성, 이후 동일한 Read View 사용
  - READ COMMITTED: 매 SELECT마다 새 Read View 생성
- [ ] **Undo Log 체인**: 같은 행에 여러 트랜잭션이 UPDATE하면 undo log가 체인처럼 연결된다. 긴 트랜잭션이 undo log purge를 막아 디스크 사용량이 늘어나는 이유

### Step 4: Spring @Transactional 매핑

- [ ] `@Transactional(isolation = Isolation.READ_COMMITTED)`가 실제로 MySQL에 `SET TRANSACTION ISOLATION LEVEL READ COMMITTED`를 보내는지 p6spy로 확인
- [ ] `@Transactional(readOnly = true)`가 실제로 하는 일:
  - JPA: flush mode를 MANUAL로 변경 → dirty checking 스킵
  - JDBC: `connection.setReadOnly(true)` → MySQL에서 특별한 효과 없음 (DataSource 라우팅에 활용)
- [ ] `propagation` 실험:
  - `REQUIRED` (기본값): 기존 트랜잭션이 있으면 참여
  - `REQUIRES_NEW`: **새 커넥션을 획득해서** 별도 트랜잭션 실행
    - ⚠️ 주의: 외부 트랜잭션이 커넥션 1개를 점유한 상태에서 내부 `REQUIRES_NEW`가 커넥션 1개를 추가 점유 → HikariCP pool size가 작으면 **데드락** 발생 가능

```java
// 위험한 패턴 예시
@Transactional
public void outer() {
    // 커넥션 1개 점유
    innerService.inner();  // REQUIRES_NEW → 커넥션 1개 추가 필요
    // pool size가 1이면 여기서 데드락!
}
```

### Step 5: 트랜잭션과 JPA 영속성 컨텍스트 관계

- [ ] 트랜잭션 커밋 시 영속성 컨텍스트가 flush되는 시점 확인
- [ ] `@Transactional`이 없는 상태에서 `save()` 호출 시 auto-commit 모드 동작 확인
- [ ] OSIV(Open Session In View) 설정이 트랜잭션 범위에 미치는 영향

---

## 테스트 작성 가이드

격리 수준 테스트는 **2개의 스레드**로 동시 트랜잭션을 시뮬레이션한다.

```java
@DisplayName("READ COMMITTED에서 다른 트랜잭션의 커밋된 변경이 보인다 (Non-Repeatable Read)")
@Test
void nonRepeatableRead() throws Exception {
    CountDownLatch sessionBUpdated = new CountDownLatch(1);
    CountDownLatch sessionAFirstRead = new CountDownLatch(1);

    // 세션 A: READ COMMITTED로 읽기
    Future<List<Integer>> sessionA = executor.submit(() -> {
        return transactionTemplate.execute(status -> {
            // 1차 읽기
            int firstRead = couponRepository.findDiscountById(couponId);
            sessionAFirstRead.countDown();

            // 세션 B의 업데이트 대기
            sessionBUpdated.await();

            // 2차 읽기 — 값이 달라져야 한다
            int secondRead = couponRepository.findDiscountById(couponId);
            return List.of(firstRead, secondRead);
        });
    });

    // 세션 B: 값 변경 후 커밋
    executor.submit(() -> {
        sessionAFirstRead.await();
        transactionTemplate.execute(status -> {
            couponRepository.updateDiscount(couponId, 5000);
            return null;
        });
        sessionBUpdated.countDown();
    });

    List<Integer> reads = sessionA.get(5, TimeUnit.SECONDS);
    assertThat(reads.get(0)).isNotEqualTo(reads.get(1));  // Non-Repeatable Read!
}
```

**주의사항**:
- `@Transactional`을 테스트 메서드에 붙이면 안 된다 (테스트 자체가 하나의 트랜잭션이 되어버림)
- `TransactionTemplate`이나 `PlatformTransactionManager`를 직접 사용
- `CountDownLatch`로 두 스레드의 실행 순서를 정밀하게 제어
- 타임아웃을 반드시 설정 (데드락 시 테스트가 영원히 안 끝남)

---

## 검증 체크리스트

- [ ] "READ COMMITTED와 REPEATABLE READ의 MVCC Read View 생성 시점 차이는?"
- [ ] "InnoDB의 REPEATABLE READ가 표준 SQL의 REPEATABLE READ와 다른 점은?"
- [ ] "`@Transactional(propagation = REQUIRES_NEW)`가 커넥션 풀에 미치는 영향은?"
- [ ] "긴 트랜잭션이 시스템에 미치는 부작용 3가지를 설명하라" (undo log 비대화, 락 점유 시간 증가, 커넥션 점유)

---

## 학습 자료

- [MySQL 8.0 공식 문서 — Transaction Isolation Levels](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html)
- [MySQL 8.0 공식 문서 — InnoDB Multi-Versioning](https://dev.mysql.com/doc/refman/8.0/en/innodb-multi-versioning.html)
- [Real MySQL 8.0 (위키북스)](https://product.kyobobook.co.kr/detail/S000001766482) — 5장 트랜잭션과 잠금
- 10분 테코톡 — 트랜잭션 격리 수준 관련 영상들
- [Spring @Transactional 공식 문서](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html)

---

## 다음 모듈 연결

트랜잭션 격리 수준을 이해했다면 **module-lock**으로 넘어간다. "REPEATABLE READ에서 Phantom Read를 막기 위해 Gap Lock이 필요하다"는 것을 체감할 수 있다. 또한 "두 트랜잭션이 같은 행을 동시에 수정하면 어떤 일이 발생하는가?"라는 질문이 자연스럽게 락 학습으로 이어진다.
