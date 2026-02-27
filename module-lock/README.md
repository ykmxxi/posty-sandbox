# module-lock: 락 학습

> **핵심 질문**: InnoDB는 동시성을 어떻게 제어하는가?

## 학습 목표

이 모듈을 마치면 다음을 설명할 수 있어야 한다:

- 낙관적 락과 비관적 락의 동작 원리, 그리고 "어떤 상황에서 어떤 락을 선택하는지" 판단 기준
- InnoDB의 Record Lock, Gap Lock, Next-Key Lock이 각각 어떤 범위를 잠그는지
- 인덱스가 없는 컬럼으로 WHERE 조건을 걸면 왜 테이블 전체가 잠기는지
- 데드락이 발생하는 조건과 InnoDB의 데드락 감지 메커니즘
- `performance_schema.data_locks`로 현재 락 상태를 모니터링하는 방법

## 선행 지식

- **module-index 완료**: 락은 인덱스 레코드에 걸린다. B+Tree 구조를 모르면 Gap Lock의 범위를 이해할 수 없다.
- **module-transaction 완료**: 격리 수준이 락의 종류와 범위를 결정한다.

---

## 학습 로드맵

이 모듈은 3개의 Part로 나뉜다. Part 1(애플리케이션 레벨) → Part 2(InnoDB 내부 락) → Part 3(분산 락) 순서로 학습한다.

---

### Part 1: 애플리케이션 레벨 락

JPA/Spring에서 제공하는 동시성 제어 메커니즘을 먼저 다룬다.

#### Step 1-1: 동시성 문제 체험

- [ ] 쿠폰 재고 100개, 100개 스레드가 동시에 1개씩 차감 → 결과가 0이 아닌 문제 재현
  - 아무런 락 없이 `quantity -= 1` 을 수행하면 Lost Update 발생
  - p6spy로 실제 발생하는 쿼리 확인: `UPDATE coupon SET quantity = ? WHERE id = ?`

**핵심 포인트**: 이 문제의 본질은 "read → 계산 → write" 사이에 다른 트랜잭션이 끼어드는 것이다. 이를 해결하는 방식이 낙관적 락과 비관적 락이다.

#### Step 1-2: 낙관적 락 (Optimistic Lock)

- [ ] `@Version` 필드 추가 후 동시 수정 시 `ObjectOptimisticLockingFailureException` 발생 확인
- [ ] 실제 쿼리 확인: `UPDATE coupon SET quantity = ?, version = 2 WHERE id = ? AND version = 1`
  - version이 맞지 않으면 UPDATE 영향 행 수 = 0 → JPA가 예외를 던진다
- [ ] 재시도 로직 구현: `@Retryable` 또는 직접 while 루프
- [ ] **트레이드오프 분석**:
  - 장점: DB 락을 잡지 않아 처리량이 높다
  - 단점: 충돌이 잦으면 재시도 비용이 커진다
  - 선택 기준: **읽기 비율이 높고 충돌이 드문 경우** 적합

#### Step 1-3: 비관적 락 (Pessimistic Lock)

- [ ] `@Lock(LockModeType.PESSIMISTIC_WRITE)` → 실제 `SELECT ... FOR UPDATE` 쿼리 확인
- [ ] 100개 스레드 동시 차감 → 정확히 0이 되는지 확인
- [ ] `SELECT ... FOR UPDATE`가 해당 행에 X Lock을 거는 것을 `performance_schema.data_locks`로 확인
- [ ] **트레이드오프 분석**:
  - 장점: 충돌이 잦아도 확실하게 직렬화된다
  - 단점: 락 대기 시간만큼 처리량이 떨어진다, 데드락 가능성
  - 선택 기준: **충돌이 빈번하고 데이터 정합성이 중요한 경우** 적합

#### Step 1-4: @DynamicUpdate와 Lost Update

- [ ] 시나리오: 매니저가 메뉴명 수정 + 주방에서 동시에 품절 처리
  - `@DynamicUpdate` 없이: `UPDATE menu SET name=?, price=?, is_sold_out=? WHERE id=?` → 한쪽 변경이 덮어씌워짐
  - `@DynamicUpdate` 있으면: `UPDATE menu SET name=? WHERE id=?` / `UPDATE menu SET is_sold_out=? WHERE id=?` → 각자 다른 컬럼만 수정
- [ ] ⚠️ `@DynamicUpdate`의 한계: 같은 컬럼을 두 트랜잭션이 수정하면 여전히 Lost Update 발생. 만능이 아니다.

---

### Part 2: InnoDB 내부 락

MySQL 콘솔에서 직접 실험한다. JPA가 아닌 **raw SQL**로 진행해야 락의 실체를 정확히 볼 수 있다.

#### Step 2-1: S Lock / X Lock 기본

- [ ] Shared Lock: `SELECT ... LOCK IN SHARE MODE` (MySQL 8.0: `SELECT ... FOR SHARE`)
  - 여러 트랜잭션이 동시에 S Lock을 잡을 수 있다
  - S Lock이 걸린 행에 UPDATE(X Lock 요청)하면 대기한다
- [ ] Exclusive Lock: `SELECT ... FOR UPDATE`
  - X Lock은 다른 모든 락(S, X)과 충돌한다

```sql
-- 세션 A
BEGIN;
SELECT * FROM coupon WHERE id = 1 FOR SHARE;
-- S Lock 획득

-- 세션 B
BEGIN;
SELECT * FROM coupon WHERE id = 1 FOR SHARE;  -- 성공 (S-S 호환)
UPDATE coupon SET quantity = 10 WHERE id = 1;  -- 대기! (S-X 비호환)
```

#### Step 2-2: Record Lock

- [ ] 인덱스 레코드에 정확히 걸리는 락
- [ ] PK 조건으로 `FOR UPDATE` → `performance_schema.data_locks`에서 `LOCK_TYPE=RECORD`, `LOCK_MODE=X,REC_NOT_GAP` 확인

```sql
SELECT * FROM performance_schema.data_locks\G
-- LOCK_TYPE: RECORD
-- LOCK_MODE: X,REC_NOT_GAP
-- LOCK_DATA: 1  (PK 값)
```

#### Step 2-3: Gap Lock

- [ ] **Gap Lock의 존재 이유**: REPEATABLE READ에서 Phantom Read를 방지하기 위해 "아직 존재하지 않는 행이 들어올 수 있는 공간"을 잠근다
- [ ] 실험: 인덱스에 값 10, 20, 30이 있을 때, `WHERE indexed_col = 15 FOR UPDATE` 실행
  - 값 15는 존재하지 않지만, 10과 20 사이의 Gap에 락이 걸린다
  - 다른 세션에서 `INSERT ... indexed_col = 12` → 대기

```sql
-- data_locks 확인
-- LOCK_MODE: X,GAP
-- LOCK_DATA: 20  (Gap Lock은 "다음 레코드 앞의 간격"에 건다)
```

- [ ] **Gap Lock의 특성**: Gap Lock끼리는 충돌하지 않는다 (두 트랜잭션이 같은 갭에 Gap Lock을 잡을 수 있음). INSERT만 차단한다.

#### Step 2-4: Next-Key Lock

- [ ] Next-Key Lock = Record Lock + 그 앞의 Gap Lock
- [ ] InnoDB REPEATABLE READ의 기본 잠금 방식
- [ ] 범위 조건 쿼리에서의 잠금 범위 확인:

```sql
-- 인덱스 값: 10, 20, 30, 40
BEGIN;
SELECT * FROM t WHERE indexed_col BETWEEN 15 AND 25 FOR UPDATE;
-- 잠기는 범위: (10, 20], (20, 30] → Next-Key Lock
-- INSERT indexed_col = 11 → 대기 (Gap Lock)
-- INSERT indexed_col = 25 → 대기 (Gap Lock)
-- UPDATE indexed_col = 10 → 성공 (범위 밖)
```

- [ ] `performance_schema.data_locks`로 실제 잠긴 범위를 눈으로 확인

#### Step 2-5: 인덱스 없는 컬럼의 락 — 왜 위험한가

- [ ] **가장 중요한 실험**: 인덱스가 없는 컬럼으로 `WHERE`를 걸면 테이블 전체에 락이 걸린다

```sql
-- status 컬럼에 인덱스 없음
BEGIN;
SELECT * FROM coupon WHERE status = 'ACTIVE' FOR UPDATE;
-- → 모든 행에 Next-Key Lock이 걸림! (Full Table Scan이므로)

-- 다른 세션에서 status = 'EXPIRED'인 행도 수정 불가
```

- [ ] 인덱스를 추가한 후 같은 쿼리 실행 → 해당 행만 잠기는 것 확인
- [ ] **실무 교훈**: `SELECT ... FOR UPDATE` 사용 시 반드시 인덱스가 있는 컬럼으로 조건을 걸어야 한다

#### Step 2-6: 데드락 재현과 분석

- [ ] 교과서적 데드락 재현:

```sql
-- 세션 A                          -- 세션 B
BEGIN;                              BEGIN;
UPDATE t SET v=1 WHERE id=1;        UPDATE t SET v=1 WHERE id=2;
-- A: id=1 X Lock 획득              -- B: id=2 X Lock 획득
UPDATE t SET v=1 WHERE id=2;        UPDATE t SET v=1 WHERE id=1;
-- A: id=2 대기 (B가 점유)            -- B: id=1 대기 (A가 점유) → 데드락!
```

- [ ] InnoDB 데드락 감지: 대기 그래프(wait-for graph)에서 사이클 발견 → 비용이 적은 트랜잭션을 롤백
- [ ] `SHOW ENGINE INNODB STATUS`에서 `LATEST DETECTED DEADLOCK` 섹션 읽는 법
- [ ] **`innodb_lock_wait_timeout`** 조절 실험: 기본값 50초, 줄여서 빠른 실패 유도
- [ ] 데드락 예방 패턴: 리소스 접근 순서를 항상 동일하게 (예: PK 오름차순)

#### Step 2-7: 기타 락 (개념 이해 + 간단 확인)

- [ ] **Insert Intention Lock**: INSERT가 Gap Lock과 공존하는 방식. 같은 갭에 서로 다른 값을 INSERT하면 충돌하지 않는다.
- [ ] **Intention Lock (IS/IX)**: 테이블 레벨의 "의도 락". 행 락을 잡기 전에 테이블에 먼저 IS/IX를 건다. DDL과 DML 간 충돌 감지용.
- [ ] **Auto-Inc Lock**: AUTO_INCREMENT 값 채번 시 테이블 수준 락. `innodb_autoinc_lock_mode` 값(0, 1, 2)에 따른 동작 차이.
- [ ] **Metadata Lock (MDL)**: `ALTER TABLE` 중 `SELECT`가 대기하는 이유. 운영 중 DDL 실행 시 주의해야 하는 이유.

---

### Part 3: 분산 환경의 락

#### Step 3-1: MySQL Named Lock (User-Level Lock)

- [ ] `GET_LOCK('coupon_issue', 10)` / `RELEASE_LOCK('coupon_issue')` 동작 확인
- [ ] Named Lock 기반 쿠폰 발급 직렬화 구현
- [ ] ⚠️ 주의: Named Lock은 **별도 커넥션**에서 관리해야 한다. 트랜잭션 커밋 시 Named Lock이 함께 해제되지 않으므로, 비즈니스 로직 커넥션과 분리한다.

```java
// Named Lock 전용 DataSource를 분리하는 패턴
public void issueWithNamedLock(Long couponId) {
    namedLockRepository.getLock("coupon_" + couponId, 10);
    try {
        couponService.issue(couponId);  // 별도 트랜잭션
    } finally {
        namedLockRepository.releaseLock("coupon_" + couponId);
    }
}
```

- [ ] Named Lock vs 비관적 락 비교: Named Lock은 논리적 리소스에 이름을 붙여 잠글 수 있어 유연하다. 비관적 락은 행 단위로만 잠금 가능.

#### Step 3-2: Redis 분산 락 (개념)

- [ ] Redisson의 `RLock` 동작 원리 개념 이해 (SET NX + TTL)
- [ ] MySQL Named Lock과의 비교: 성능, SPOF, TTL 기반 자동 해제
- [ ] (실습은 선택) Forgather 같은 서비스에서 "이벤트 앨범 생성" 같은 작업에 분산 락이 필요한 시나리오

---

## 락 모니터링 유틸리티

테스트 중 락 상태를 확인하는 헬퍼 쿼리:

```sql
-- 현재 걸린 모든 락
SELECT
    ENGINE_TRANSACTION_ID as trx_id,
    OBJECT_NAME as table_name,
    INDEX_NAME as idx,
    LOCK_TYPE,
    LOCK_MODE,
    LOCK_STATUS,
    LOCK_DATA
FROM performance_schema.data_locks
ORDER BY ENGINE_TRANSACTION_ID;

-- 락 대기 관계 (누가 누구를 기다리는지)
SELECT
    r.trx_id as waiting_trx,
    r.trx_mysql_thread_id as waiting_thread,
    b.trx_id as blocking_trx,
    b.trx_mysql_thread_id as blocking_thread,
    r.trx_query as waiting_query
FROM information_schema.INNODB_TRX r
JOIN performance_schema.data_lock_waits w ON r.trx_id = w.REQUESTING_ENGINE_TRANSACTION_ID
JOIN information_schema.INNODB_TRX b ON b.trx_id = w.BLOCKING_ENGINE_TRANSACTION_ID;
```

---

## 검증 체크리스트

- [ ] "낙관적 락은 실제로 DB에 락을 거는가? 아닌가?" (아니다 — 애플리케이션 레벨 충돌 감지)
- [ ] "인덱스가 없는 컬럼에 `SELECT ... FOR UPDATE`를 걸면 왜 전체 행이 잠기는가?"
- [ ] "Gap Lock끼리는 충돌하지 않는다. 그렇다면 Gap Lock은 무엇을 차단하는가?" (INSERT만 차단)
- [ ] "데드락이 발생했을 때 InnoDB는 어떻게 감지하고 어떤 트랜잭션을 롤백하는가?"
- [ ] "`innodb_autoinc_lock_mode = 2`일 때 Statement-Based Replication에서 문제가 생기는 이유는?"

---

## 학습 자료

- [MySQL 8.0 공식 문서 — InnoDB Locking](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html)
- [MySQL 8.0 공식 문서 — data_locks Table](https://dev.mysql.com/doc/refman/8.0/en/performance-schema-data-locks-table.html)
- [Real MySQL 8.0 (위키북스)](https://product.kyobobook.co.kr/detail/S000001766482) — 5.3 InnoDB 스토리지 엔진 잠금
- [Percona Blog — InnoDB Locking Explained](https://www.percona.com/blog/) — InnoDB locking 관련 포스트
- [우아한형제들 기술블로그 — MySQL Named Lock](https://techblog.woowahan.com/)

---

## 다음 모듈 연결

인덱스, 트랜잭션, 락을 모두 이해했다면 **module-performance**로 넘어간다. "이 인덱스를 추가하면 읽기는 빨라지지만 쓰기 시 락 경합은 어떻게 되는가?" 같은 트레이드오프를 판단할 수 있는 기반이 갖춰졌다.
