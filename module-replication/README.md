# module-replication: 복제 지연 학습

> **핵심 질문**: 읽기 부하를 어떻게 분산하고, 복제 지연은 어떻게 다루는가?

## 학습 목표

이 모듈을 마치면 다음을 설명할 수 있어야 한다:

- MySQL 비동기 복제의 동작 과정 (Binary Log → Relay Log → Apply)
- 복제 지연이 발생하는 원인과 "쓰기 후 즉시 읽기" 문제
- Spring에서 `@Transactional(readOnly)`를 활용한 Source-Replica 라우팅 구현
- 복제 지연 상황에서의 실무적 대응 전략

## 선행 지식

- **module-index ~ module-performance 완료 권장**: 단일 DB의 동작 원리를 이해한 상태에서 복제 아키텍처를 학습해야 효과적이다
- Docker Compose 기본 사용법

---

## 학습 로드맵

### Step 0: 복제 환경 구성

이 모듈은 **Docker Compose**로 Source-Replica 구성을 먼저 띄워야 한다.

- [ ] `docker/mysql-replication/` 디렉토리의 Docker Compose 실행

```bash
cd docker/mysql-replication
docker compose up -d
```

- [ ] 복제 상태 확인:

```sql
-- Replica에서 실행
SHOW REPLICA STATUS\G
-- 확인할 항목:
--   Replica_IO_Running: Yes
--   Replica_SQL_Running: Yes
--   Seconds_Behind_Source: 0
```

- [ ] ⚠️ 복제 셋업 자동화 시 주의: Source가 완전히 기동된 후 Replica에서 `CHANGE REPLICATION SOURCE TO ...`를 실행해야 한다. Docker Compose의 `healthcheck` + init 스크립트로 순서를 보장할 것.

#### GTID 기반 복제 설정

```ini
# source/my.cnf
[mysqld]
server-id=1
log-bin=mysql-bin
binlog-format=ROW
gtid-mode=ON
enforce-gtid-consistency=ON

# replica/my.cnf
[mysqld]
server-id=2
relay-log=relay-bin
read-only=1
gtid-mode=ON
enforce-gtid-consistency=ON
```

```sql
-- Replica에서 복제 시작
CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='mysql-source',
    SOURCE_USER='repl',
    SOURCE_PASSWORD='repl_password',
    SOURCE_AUTO_POSITION=1;
START REPLICA;
```

**왜 GTID인가**: Binlog position 기반 복제는 장애 복구 시 정확한 position을 찾아야 한다. GTID는 각 트랜잭션에 고유 ID를 부여해서 "어디까지 복제했는지"를 자동 추적한다. 실무에서는 GTID가 표준이다.

---

### Step 1: 복제 동작 원리 이해

- [ ] **비동기 복제 과정** 3단계:

```
Source                           Replica
  │                                │
  │ 1. 트랜잭션 커밋               │
  │    → Binary Log에 기록         │
  │                                │
  │ ──── 네트워크 전송 ──────────→ │
  │                                │
  │                    2. IO Thread: Relay Log에 기록
  │                    3. SQL Thread: Relay Log 재실행 → 데이터 반영
```

- [ ] **핵심 스레드 3개**:
  - Source의 Binlog Dump Thread: Binary Log를 Replica에게 전송
  - Replica의 IO Thread: Binary Log를 받아 Relay Log에 기록
  - Replica의 SQL Thread: Relay Log를 읽어 실제 쿼리 재실행

- [ ] Binary Log 형식 (`binlog-format`) 차이:
  - `STATEMENT`: SQL 문장 자체를 기록 → 비결정적 함수(NOW(), RAND())에서 Source-Replica 불일치 가능
  - `ROW`: 변경된 행 데이터를 기록 → 안전하지만 로그 크기 증가
  - `MIXED`: MySQL이 자동 판단 → 실무에서는 **ROW가 표준**

### Step 2: 복제 지연 재현

- [ ] **복제 지연 발생시키기**:

```sql
-- 방법 1: Replica SQL Thread 일시 정지
-- Replica에서:
STOP REPLICA SQL_THREAD;
-- Source에서 여러 INSERT 실행
-- Replica에서:
SHOW REPLICA STATUS\G  -- Seconds_Behind_Source > 0 확인
START REPLICA SQL_THREAD;  -- 다시 시작하면 따라잡는 것 확인
```

```sql
-- 방법 2: Source에서 대량 쓰기로 자연스러운 지연 유도
-- Source에서 10만 건 INSERT → Replica가 따라잡는 데 시간 소요
```

- [ ] `Seconds_Behind_Source` 값이 증가하다 다시 0으로 돌아오는 과정 관찰

### Step 3: "쓰기 후 즉시 읽기" 문제 재현

- [ ] **시나리오**: 사용자가 쿠폰을 발급받고 → 바로 "내 쿠폰 목록" 조회

```java
// Source에 쓰기
couponService.issue(userId, couponId);  // INSERT → Source

// 바로 Replica에서 읽기
List<Coupon> myCoupons = couponService.getMyCoupons(userId);  // SELECT → Replica
// → 복제 지연 시 방금 발급한 쿠폰이 안 보임!
```

- [ ] 이 문제를 테스트 코드로 재현 (Replica SQL Thread를 잠시 멈추는 트릭 활용)

### Step 4: Spring DataSource 라우팅 구현

- [ ] `AbstractRoutingDataSource`를 활용한 Source/Replica 분기:

```java
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager
            .isCurrentTransactionReadOnly();
        return isReadOnly ? "replica" : "source";
    }
}
```

- [ ] `@Transactional(readOnly = true)` → Replica로 라우팅
- [ ] `@Transactional` (기본값) → Source로 라우팅
- [ ] p6spy로 실제로 어느 DataSource에 쿼리가 가는지 확인

#### 주의: LazyConnectionDataSourceProxy

- [ ] `AbstractRoutingDataSource`만 쓰면 트랜잭션 시작 시점에 커넥션을 확보하는데, 이때는 아직 `readOnly` 여부가 결정되지 않을 수 있다
- [ ] `LazyConnectionDataSourceProxy`로 감싸면 **실제 쿼리 실행 시점**까지 커넥션 획득을 지연한다

```java
@Bean
public DataSource dataSource() {
    RoutingDataSource routingDataSource = new RoutingDataSource();
    // ... source, replica DataSource 설정
    return new LazyConnectionDataSourceProxy(routingDataSource);
}
```

### Step 5: 복제 지연 대응 전략

- [ ] **전략 1: 쓰기 후 Source에서 읽기**
  - 특정 시나리오(마이페이지 직후 등)에서 `readOnly = false`로 강제하여 Source에서 읽기
  - 단점: Source 부하 증가, 라우팅 로직 복잡

- [ ] **전략 2: 세션 기반 일관성**
  - Source에 쓴 후 일정 시간(예: 5초) 동안은 같은 유저의 읽기도 Source로 보내기
  - 세션 또는 Redis에 "최근 쓰기 시각" 저장

- [ ] **전략 3: 반동기(Semi-Synchronous) 복제**
  - Source가 커밋 시 최소 1개 Replica가 Relay Log에 기록할 때까지 대기
  - 복제 지연은 줄지만 쓰기 성능이 저하된다

```sql
-- Source에서 Semi-Sync 활성화
INSTALL PLUGIN rpl_semi_sync_source SONAME 'semisync_source.so';
SET GLOBAL rpl_semi_sync_source_enabled = 1;
```

- [ ] **전략 4: 복제 지연 모니터링 기반 자동 전환**
  - `Seconds_Behind_Source`가 임계값(예: 3초)을 초과하면 읽기도 Source로 전환
  - 모니터링 → 알림 → 자동 전환 파이프라인

#### Forgather 적용 시나리오

- [ ] "호스트가 이벤트 생성 → 바로 이벤트 상세 페이지로 리다이렉트" → 복제 지연 시 404 가능
- [ ] 해결: 이벤트 생성 API의 응답에 생성된 데이터를 포함하여 클라이언트가 즉시 표시하고, 이후 새로고침 시 Replica에서 읽기

### Step 6: 복제 모니터링

- [ ] 핵심 모니터링 지표:

```sql
SHOW REPLICA STATUS\G
-- Seconds_Behind_Source: 복제 지연 (초)
-- Replica_IO_Running / Replica_SQL_Running: 복제 스레드 상태
-- Last_Error: 마지막 에러 (스키마 불일치 등)
-- Retrieved_Gtid_Set vs Executed_Gtid_Set: GTID 기반 지연 확인
```

- [ ] (선택) Grafana + Prometheus로 `Seconds_Behind_Source`를 시계열 그래프로 시각화

---

## 검증 체크리스트

- [ ] "MySQL 비동기 복제에서 데이터 유실이 발생할 수 있는 시나리오는?"
  (Source가 커밋 후 Binary Log를 Replica에 보내기 전에 장애 발생)
- [ ] "`@Transactional(readOnly = true)`가 Replica로 라우팅되려면 어떤 설정이 필요한가?"
- [ ] "복제 지연 시 '쓰기 후 즉시 읽기' 문제를 해결하는 방법 3가지를 설명하라"
- [ ] "Semi-Synchronous 복제가 데이터 유실 문제를 완전히 해결하는가?" (아니다 — Relay Log에 기록될 뿐, Apply까지 보장하지 않음)
- [ ] "binlog-format=ROW가 STATEMENT보다 안전한 이유는?"

---

## 학습 자료

- [MySQL 8.0 공식 문서 — Replication](https://dev.mysql.com/doc/refman/8.0/en/replication.html)
- [MySQL 8.0 공식 문서 — GTID](https://dev.mysql.com/doc/refman/8.0/en/replication-gtids.html)
- [MySQL 8.0 공식 문서 — Semisynchronous Replication](https://dev.mysql.com/doc/refman/8.0/en/replication-semisync.html)
- [Real MySQL 8.0 (위키북스)](https://product.kyobobook.co.kr/detail/S000001766482) — 16장 복제
- [High Performance MySQL (O'Reilly)](https://www.oreilly.com/library/view/high-performance-mysql/9781492080503/) — Replication 챕터
- [우아한형제들 기술블로그 — DB 읽기 분산](https://techblog.woowahan.com/)

---

## 전체 학습 완료 후

5개 모듈을 모두 마쳤다면, 다음 심화 주제를 고려할 수 있다:

- **Connection Pooling 심화**: HikariCP 내부 동작, 적정 pool size 산출 (`connections = (core_count * 2) + disk_spindles`)
- **MySQL 옵티마이저 심화**: 히스토그램, Index Merge, 옵티마이저 힌트
- **읽기 부하 분산 아키텍처**: ProxySQL, MySQL Router, HAProxy를 통한 자동 라우팅
- **캐싱 전략**: Redis를 활용한 읽기 캐시, Cache-Aside/Write-Through/Write-Behind 패턴
- **대용량 트래픽 대응**: Rate Limiting, Circuit Breaker, 큐 기반 비동기 처리
