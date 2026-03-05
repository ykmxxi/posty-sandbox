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

- [x] **원자성(Atomicity)** — InnoDB vs MyISAM 비교

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

- [x] 이 차이가 발생하는 이유: InnoDB는 undo log를 통한 롤백을 지원하지만, MyISAM은 트랜잭션을 지원하지 않는다

> InnoDB는 각 DML(INSERT/UPDATE/DELETE) 수행 직전에 해당 행의 변경 전 데이터를 undo log에 기록한다. ROLLBACK 시 이 undo log를 역순으로 적용하여 변경을 되돌린다.
> "트랜잭션 시작 시점의 전체 스냅샷을 뜬다"는 것이 아니라, **변경이 일어나는 순간마다 개별적으로** 기록한다.
>
> undo log 타입 구분:
> - **insert undo log**: INSERT의 역연산(해당 행 DELETE) 기록. 해당 트랜잭션의 롤백에만 필요하므로 커밋 즉시 purge 가능
> - **update undo log**: UPDATE/DELETE의 역연산(이전 컨럼값) 기록. **MVCC 읽기에도 사용**되므로, 해당 undo log를 참조하는 Read View가 모두 사라질 때까지 purge할 수 없음

- [x] **autocommit과 암묵적 트랜잭션**: MySQL의 `autocommit=1`(기본값) 상태에서 각 SQL문이 암묵적 트랜잭션으로 감싸지는 동작 이해

> **왜 MySQL은 autocommit=1을 기본값으로 가져가는가?**
>
> **긴 트랜잭션 사고 방지**: `autocommit=0`이 기본이면, 개발자가 `BEGIN`은 했는데 `COMMIT`/`ROLLBACK`을 빠뜨리는 실수가 발생할 수 있다. 이 경우 트랜잭션이 열린 채로 커넥션이 유지되어 락 점유, undo log 비대화, 커넥션 풀 고갈이 발생한다. `autocommit=1`이면 명시적 `BEGIN` 없이는 각 SQL이 즉시 커밋되므로 이런 사고를 구조적으로 방지한다.
>
> **단순성**: 대부분의 단일 쿼리 작업(단순 SELECT, 단건 INSERT 등)은 별도의 트랜잭션 경계가 필요 없다. 여러 SQL을 하나의 원자적 단위로 묶어야 할 때만 명시적 `BEGIN`~`COMMIT`을 쓰는 것이 더 안전한 설계다.
>
> 참고: PostgreSQL도 autocommit이 기본이지만, 클라이언트 드라이버 레벨에서 처리하는 반면 MySQL은 서버 레벨 설정이라는 차이가 있다.

- [x] **내구성(Durability)**: WAL(Write-Ahead Logging) 원칙 — 데이터 페이지에 쓰기 전에 redo log에 먼저 기록하는 이유 (순차 I/O vs 랜덤 I/O)

> MySQL은 WAL 원칙을 따른다. 데이터 페이지를 직접 디스크에 쓰지 않고, 먼저 redo log에 변경 내용을 기록한 뒤 커밋을 완료한다. 실제 데이터 페이지 반영은 나중에 비동기로 처리한다.
>
> **redo log가 순차 I/O인 이유**: redo log는 **append-only 구조의 순환 파일**이다. 항상 끝에 이어서 쓰기만 하므로 디스크 헤드가 한 방향으로만 이동한다. 반면 데이터 페이지 쓰기는 수정 대상 페이지가 디스크 여기저기에 흩어져 있으므로 랜덤 I/O가 발생한다.
>
> **세컨더리 인덱스와의 관계**: 하나의 UPDATE가 클러스터드 인덱스 페이지 + 세컨더리 인덱스 페이지(여러 개일 수 있음) 등 **여러 페이지에 걸쳐 랜덤 I/O**를 발생시킨다. WAL로는 이 모든 변경을 redo log 한 곳에 순차적으로 기록하고, 실제 페이지 반영은 나중에 모아서 한다. InnoDB는 세커더리 인덱스 변경을 **Change Buffer**에 버퍼링하여 랜덤 I/O를 추가로 줄이기도 한다.
>
> **dirty page와 checkpoint**: 버퍼 풀에서 변경되었지만 아직 디스크에 반영되지 않은 페이지를 dirty page라 한다. InnoDB는 백그라운드에서 주기적으로 dirty page를 디스크에 flush하는데 이 과정을 checkpoint라 한다. checkpoint가 완료된 redo log 영역은 재사용할 수 있다.

- [x] `innodb_flush_log_at_trx_commit` 설정값(0, 1, 2)에 따른 redo log flush 전략: `write()` vs `fsync()` 구분, MySQL 크래시 vs OS 크래시 안전성 차이

> `write()`와 `fsync()`는 다른 연산이다. `write()`는 프로세스 메모리(log buffer)에서 OS의 page cache로 복사하는 것이고, `fsync()`는 page cache의 내용을 실제 디스크 플래터에 기록하도록 OS에 요청하는 것이다.
>
> - **값 1** (기본값): 매 커밋마다 `write()` + `fsync()`. 디스크까지 도달하므로 MySQL 크래시든 OS 크래시든 안전. **ACID-D를 완전히 보장하는 유일한 설정.**
> - **값 2**: 매 커밋마다 `write()`는 하지만 `fsync()`는 1초마다. OS page cache까지는 도달하므로 MySQL 프로세스가 죽어도 OS가 살아있으면 안전. **OS 크래시(커널 패닉, 정전 등) 시 최대 1초 유실.**
> - **값 0**: `write()`도 `fsync()`도 1초마다. 커밋 시 아무 I/O도 하지 않음. **MySQL 프로세스 크래시에도 최대 1초 유실 가능.**
>
> 실무 적용: 데이터 유실이 허용되지 않는 서비스(금융, 결제 등)는 반드시 1, 로그성 데이터나 대량 배치 처리에서는 성능을 위해 2를 사용하는 경우도 있다.

- [x] **undo log vs redo log 역할 구분**: undo log는 롤백(원자성) + MVCC 읽기용, redo log는 크래시 복구(내구성)용

> | 구분 | undo log | redo log |
> |------|----------|----------|
> | 목적 | 트랜잭션 롤백(원자성) + MVCC 스냅샷 읽기(격리성) | 크래시 복구(내구성) |
> | 기록 시점 | 각 DML 수행 직전 | 데이터 페이지를 버퍼 풀에서 변경한 직후, 커밋 전 |
> | 기록 내용 | **변경 전** 데이터 (역연산을 위한 이전 버전) | **변경 후** 데이터 (물리적 변경 — "어떤 페이지의 어떤 오프셋에 어떤 값을 썼는가") |
> | 위치 | undo tablespace (별도 undo 파일 또는 시스템 테이블스페이스) | redo log 파일 (순환 구조, MySQL 8.0.30+에서는 `#innodb_redo` 디렉터리) |
>
> 한 줄 요약: undo log는 **"변경 전"을**, redo log는 **"변경 후"를** 기록한다. 하나는 되돌리기 위해, 하나는 다시 적용하기 위해 존재한다.

### Step 2: 격리 수준과 읽기 부정합 재현

여기가 이 모듈의 핵심이다. **2개의 MySQL 세션(커넥션)**을 동시에 열어서 실험한다.

#### 선행 개념: Consistent Non-locking Read vs Locking Read (Current Read)

이 구분을 먼저 이해해야 격리 수준 실험이 의미 있다.

| 구분 | Consistent Non-locking Read (스냅샷 읽기) | Locking Read (Current Read) |
|------|------|------|
| SQL | 일반 `SELECT` | `SELECT ... FOR UPDATE`, `SELECT ... LOCK IN SHARE MODE` |
| 읽는 데이터 | MVCC 스냅샷 (undo log의 과거 버전) | **현재 시점**의 최신 커밋 데이터 |
| 락 획득 | 없음 (논블로킹) | 행에 락을 걸음 |
| 사용 시점 | 일반적인 조회 | UPDATE/DELETE의 WHERE 절, 동시성 제어가 필요한 조회 |

> `UPDATE`, `DELETE`, `INSERT`도 내부적으로 Current Read를 수행한다. WHERE 조건에 매칭되는 행을 찾을 때 항상 최신 커밋 데이터를 기준으로 한다.
>
> **이것이 중요한 이유**: Step 2-4에서 일반 SELECT(5건)와 `SELECT ... FOR UPDATE`(6건)의 결과가 다른 것이 바로 이 차이 때문이다.

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
- [ ] 실무에서 SERIALIZABLE을 거의 사용하지 않는 이유: 모든 읽기에 공유 락을 걸어 동시성이 극단적으로 떨어진다

#### 2-6. Lost Update 문제 (Write-Write 충돌)

읽기 부정합(Dirty/Non-Repeatable/Phantom)은 읽기-쓰기 충돌이다. 하지만 쓰기-쓰기 충돌도 중요한 문제다.

- [ ] 세션 A: `SELECT balance FROM account WHERE id = 1` → 1000원
- [ ] 세션 B: `SELECT balance FROM account WHERE id = 1` → 1000원
- [ ] 세션 A: `UPDATE account SET balance = 1000 - 300 WHERE id = 1` → COMMIT (700원)
- [ ] 세션 B: `UPDATE account SET balance = 1000 - 500 WHERE id = 1` → COMMIT (500원)
- [ ] 최종 결과: 500원 (세션 A의 차감 300원이 유실됨)

**핵심 포인트**: Lost Update는 격리 수준만으로는 방지할 수 없다. `SELECT ... FOR UPDATE`로 배타적 락을 획득하거나, 낙관적 락(버전 컨트롤)을 사용해야 한다. 이 주제는 module-lock에서 더 깊이 다룬다.

#### 격리 수준별 읽기 부정합 요약

| 격리 수준 | Dirty Read | Non-Repeatable Read | Phantom Read |
|------|------|------|------|
| READ UNCOMMITTED | O | O | O |
| READ COMMITTED | X | O | O |
| REPEATABLE READ | X | X | Δ (InnoDB는 MVCC로 대부분 방지) |
| SERIALIZABLE | X | X | X |

### Step 3: MVCC 내부 동작 이해 (개념)

#### 3-1. InnoDB 히든 컨럼

InnoDB는 모든 행에 사용자에게 보이지 않는 3개의 숨겼진 컨럼을 저장한다:

- [ ] **DB_TRX_ID**: 이 행을 마지막으로 수정한 트랜잭션 ID
- [ ] **DB_ROLL_PTR**: undo log 레코드를 가리키는 포인터 (이전 버전으로의 링크)
- [ ] **DB_ROW_ID**: PK가 없는 테이블에서 자동 생성되는 행 ID

> 이 숨겼진 커럼이 MVCC의 물리적 기반이다. DB_TRX_ID로 "누가 수정했는지", DB_ROLL_PTR로 "수정 전 데이터는 무엇인지"를 추적한다.

#### 3-2. Undo Log의 이중 역할

- [ ] **롤백용**: 트랜잭션 ROLLBACK 시 이전 데이터를 복원 (원자성 보장)
- [ ] **MVCC 읽기용**: 다른 트랜잭션이 수정 중인 행의 이전 버전을 제공 (격리성 보장)

> Step 1에서는 undo log를 "롤백 메커니즘"으로만 다룠다. 여기서는 MVCC에서의 두 번째 역할을 이해한다.

#### 3-3. Read View 가시성 판단 알고리즘

- [ ] **Read View**: 트랜잭션이 생성하는 "보이는 트랜잭션 목록". 핵심 구성요소:
  - `m_ids`: Read View 생성 시점에 활성 상태(아직 커밋 안 된)인 트랜잭션 ID 목록
  - `m_low_limit_id`: 현재까지 할당된 가장 큰 trx_id + 1 (이 이상이면 무조건 안 보임)
  - `m_up_limit_id`: m_ids 중 가장 작은 trx_id (이 미만이면 무조건 보임)
  - `m_creator_trx_id`: Read View를 생성한 트랜잭션 자신의 ID

- [ ] 행의 DB_TRX_ID를 기준으로 가시성 판단:
  1. `DB_TRX_ID == m_creator_trx_id` → 자기 자신의 변경 → **보임**
  2. `DB_TRX_ID < m_up_limit_id` → Read View 생성 전에 이미 커밋된 트랜잭션 → **보임**
  3. `DB_TRX_ID >= m_low_limit_id` → Read View 생성 후에 시작된 트랜잭션 → **안 보임**
  4. `m_up_limit_id <= DB_TRX_ID < m_low_limit_id` → m_ids 목록에 있으면 **안 보임**, 없으면 **보임**
  5. 안 보이면 → DB_ROLL_PTR를 따라 undo log 체인에서 보이는 버전을 찾을 때까지 반복

- [ ] **Read View 생성 시점 차이** (격리 수준 차이의 핵심):
  - REPEATABLE READ: 트랜잭션 최초 SELECT 시 Read View 생성, 이후 동일한 Read View 사용
  - READ COMMITTED: 매 SELECT마다 새 Read View 생성 → 그래서 다른 트랜잭션의 커밋이 즉시 보인다

#### 3-4. Undo Log 체인과 긴 트랜잭션 부작용

- [ ] 같은 행에 여러 트랜잭션이 UPDATE하면 undo log가 체인처럼 연결된다
- [ ] 긴 트랜잭션이 시스템에 미치는 부작용 3가지:
  1. **undo log purge 지연**: 해당 트랜잭션의 Read View가 참조하는 undo log를 삭제할 수 없음 → 디스크 사용량 증가
  2. **락 점유 시간 증가**: 트랜잭션이 획득한 락을 커밋/롤백할 때까지 보유 → 다른 트랜잭션 대기/데드락 위험 증가
  3. **커넥션 점유**: 트랜잭션 동안 DB 커넥션을 반납하지 않음 → HikariCP 풀 고갈 위험

### Step 4: Spring @Transactional 매핑

#### 4-1. 프록시 메커니즘과 self-invocation 문제

- [ ] `@Transactional`은 **AOP 프록시**를 통해 동작한다. 스프링 컨테이너가 빈을 등록할 때 프록시 객체로 감싸는데, 외부에서 호출해야 프록시를 거치고, **같은 클래스 내부에서 `this.method()` 호출(self-invocation)은 프록시를 우회**하므로 `@Transactional`이 적용되지 않는다.

```java
// ❌ @Transactional이 동작하지 않는 패턴
@Service
public class OrderService {
    public void createOrder() {
        this.saveOrderInternal();  // self-invocation → 프록시 우회!
    }

    @Transactional
    public void saveOrderInternal() { ... }
}

// ✅ 해결: 별도 빈으로 분리하여 프록시를 거치게 한다
```

#### 4-2. 예외 롤백 규칙

- [ ] Spring 기본 동작: **unchecked exception (`RuntimeException`, `Error`)은 롤백**, **checked exception은 커밋**
- [ ] `@Transactional(rollbackFor = Exception.class)`: checked exception도 롤백하도록 명시적 지정
- [ ] `@Transactional(noRollbackFor = SomeException.class)`: 특정 예외에서는 롤백하지 않도록 지정
- [ ] 실무에서 checked exception을 던지는 서비스 메서드에 `rollbackFor`를 빠뜨려 커밋되는 버그 경험해보기 (p6spy로 COMMIT/ROLLBACK 로그 확인)

#### 4-3. 격리 수준 매핑

- [ ] `@Transactional(isolation = Isolation.READ_COMMITTED)`가 실제로 MySQL에 `SET TRANSACTION ISOLATION LEVEL READ COMMITTED`를 보내는지 p6spy로 확인

#### 4-4. readOnly 동작 상세

- [ ] `@Transactional(readOnly = true)`가 실제로 하는 일:
  - **JPA 계층**: flush mode를 MANUAL로 변경 → dirty checking 스킵 → 스냅샷 복사본을 만들지 않아 **메모리 절약**
  - **JDBC 계층**: `connection.setReadOnly(true)` → MySQL 5.6.4+에서는 **읽기 전용 트랜잭션 최적화** 적용 (transaction ID 할당 생략, undo log 생성 감소)
  - **DataSource 라우팅**: 읽기 전용 커넥션을 리플리카 DB로 라우팅하는 데 활용 가능 (예: `AbstractRoutingDataSource`)

#### 4-5. timeout 설정

- [ ] `@Transactional(timeout = 5)`: 5초 이내에 트랜잭션이 완료되지 않으면 롤백
- [ ] MySQL의 `innodb_lock_wait_timeout`(락 대기 타임아웃, 기본 50초)과는 다른 개념: Spring timeout은 트랜잭션 전체 시간, MySQL timeout은 개별 락 대기 시간

#### 4-6. propagation 실험

- [ ] `REQUIRED` (기본값): 기존 트랜잭션이 있으면 참여. 내부에서 예외 발생 시 외부 트랜잭션도 함께 롤백됨에 주의 (`UnexpectedRollbackException`)
- [ ] `REQUIRES_NEW`: **새 커넥션을 획득해서** 별도 트랜잭션 실행
  - ⚠️ 주의: 외부 트랜잭션이 커넥션 1개를 점유한 상태에서 내부 `REQUIRES_NEW`가 커넥션 1개를 추가 점유 → HikariCP pool size가 작으면 **데드락** 발생 가능
- [ ] `NESTED`: **같은 커넥션** 내에서 Savepoint를 사용. 내부 실패 시 Savepoint까지만 롤백하고, 외부 트랜잭션은 계속 진행 가능 (JPA에서는 flush 시점 때문에 제약이 있음)
- [ ] `SUPPORTS`, `NOT_SUPPORTED`, `MANDATORY`, `NEVER`: 특수 상황에서 사용, 개념만 이해

```java
// REQUIRES_NEW 커넥션 데드락 위험 패턴
@Transactional
public void outer() {
    // 커넥션 1개 점유
    innerService.inner();  // REQUIRES_NEW → 커넥션 1개 추가 필요
    // pool size가 1이면 여기서 데드락!
}

// REQUIRED에서의 UnexpectedRollbackException
@Transactional
public void outer() {
    try {
        innerService.inner();  // REQUIRED + RuntimeException → 트랜잭션 rollback-only 마크
    } catch (Exception e) {
        // 예외를 잡아도 이미 rollback-only로 마크된 상태
    }
    // outer의 COMMIT 시도 시 UnexpectedRollbackException 발생!
}
```

### Step 5: 트랜잭션과 JPA 영속성 컨텍스트 관계

#### 5-1. flush 시점 이해

- [ ] `FlushModeType.AUTO` (기본값): 커밋 직전 + **JPQL 실행 전**에 자동 flush. JPQL이 DB를 직접 조회하므로, 영속성 컨텍스트의 변경사항을 먼저 DB에 반영해야 일관성이 유지된다
- [ ] `readOnly = true` 시 flush mode가 MANUAL로 변경되어 dirty checking과 auto-flush가 발생하지 않음
- [ ] **flush ≠ commit**: flush는 SQL을 DB에 전송하지만 트랜잭션 내에 있음. 롤백하면 flush된 SQL도 되돌려진다.

#### 5-2. `@Transactional` 없이 repository 호출 시 동작

- [ ] `SimpleJpaRepository`는 클래스 레벨에 `@Transactional(readOnly = true)`가, `save()` 등 쓰기 메서드에는 `@Transactional`이 붙어있다
- [ ] 따라서 서비스 레이어에 `@Transactional`이 없어도 repository의 각 메서드는 개별적으로 트랜잭션이 적용된다 (단, 여러 메서드를 하나의 트랜잭션으로 묶을 수 없다)
- [ ] auto-commit 모드에서는 `save()` 후 즉시 커밋되므로, 여러 `save()`를 리열해도 원자성이 보장되지 않음

#### 5-3. OSIV(Open Session In View)

- [ ] Spring Boot의 `spring.jpa.open-in-view` 기본값은 **true** (주의: 기동 시 WARNING 로그 발생)
- [ ] OSIV=true: `EntityManager`가 컨트롤러 레이어까지 열려있음 → View에서 Lazy Loading 가능. 하지만 **커넥션을 View 렌더링까지 점유**하므로 커넥션 풀 고갈 위험
- [ ] OSIV=false: 트랜잭션 종료 시 EntityManager도 닫힘 → 트랜잭션 밖에서 Lazy Loading 시 **LazyInitializationException** 발생
- [ ] 실무 권장: OSIV=false + 필요한 데이터를 서비스 레이어에서 fetch join으로 미리 로딩

#### 5-4. 영속성 컨텍스트 라이프사이클과 트랜잭션

- [ ] 기본적으로 영속성 컨텍스트는 **트랜잭션 범위와 동일**한 생명주기를 가진다 (Transaction-scoped)
- [ ] 트랜잭션 커밋/롤백 시 영속성 컨텍스트도 함께 종료 → 1차 캐시 소멸

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

**격리 수준 설정 방법**:

```java
// TransactionTemplate으로 격리 수준 지정
TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
txTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

txTemplate.execute(status -> {
    // READ COMMITTED로 실행되는 코드
    return couponRepository.findDiscountById(couponId);
});
```

---

## 검증 체크리스트

- [ ] "READ COMMITTED와 REPEATABLE READ의 MVCC Read View 생성 시점 차이는?"
- [ ] "InnoDB의 REPEATABLE READ가 표준 SQL의 REPEATABLE READ와 다른 점은?"
- [ ] "`@Transactional(propagation = REQUIRES_NEW)`가 커넥션 풀에 미치는 영향은?"
- [ ] "긴 트랜잭션이 시스템에 미치는 부작용 3가지를 설명하라" (undo log 비대화, 락 점유 시간 증가, 커넥션 점유)
- [ ] "undo log와 redo log의 역할 차이를 설명하라"
- [ ] "Consistent Non-locking Read와 Locking Read(Current Read)의 차이는? UPDATE는 어느 쪽인가?"
- [ ] "@Transactional self-invocation 문제가 발생하는 이유와 해결 방법은?"
- [ ] "Spring에서 checked exception과 unchecked exception의 트랜잭션 롤백 동작 차이는?"
- [ ] "OSIV=true일 때 발생할 수 있는 커넥션 풀 문제는?"
- [ ] "flush와 commit의 차이를 설명하라"
- [ ] "Read View의 가시성 판단 알고리즘을 DB_TRX_ID를 기준으로 설명하라"

---

## 학습 자료

- [MySQL 8.0 공식 문서 — Transaction Isolation Levels](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html)
- [MySQL 8.0 공식 문서 — InnoDB Multi-Versioning](https://dev.mysql.com/doc/refman/8.0/en/innodb-multi-versioning.html)
- [MySQL 8.0 공식 문서 — Consistent Nonlocking Reads](https://dev.mysql.com/doc/refman/8.0/en/innodb-consistent-read.html)
- [MySQL 8.0 공식 문서 — InnoDB Undo Logs](https://dev.mysql.com/doc/refman/8.0/en/innodb-undo-logs.html)
- [Real MySQL 8.0 (위키북스)](https://product.kyobobook.co.kr/detail/S000001766482) — 5장 트랜잭션과 잠금
- 10분 테코톡 — 트랜잭션 격리 수준 관련 영상들
- [Spring @Transactional 공식 문서](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html)
- [Baeldung — Transaction Propagation and Isolation in Spring @Transactional](https://www.baeldung.com/spring-transactional-propagation-isolation)

---

## 다음 모듈 연결

트랜잭션 격리 수준을 이해했다면 **module-lock**으로 넘어간다. "REPEATABLE READ에서 Phantom Read를 막기 위해 Gap Lock이 필요하다"는 것을 체감할 수 있다. 또한 "두 트랜잭션이 같은 행을 동시에 수정하면 어떤 일이 발생하는가?"라는 질문이 자연스럽게 락 학습으로 이어진다.
