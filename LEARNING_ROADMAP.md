# MySQL 고급 학습 로드맵

## 학습 순서

```
[1] 인덱스 → [2] 트랜잭션 → [3] 락 → [4] 성능 개선 → [5] 복제 지연
```

### 왜 이 순서인가?

각 모듈은 이전 모듈의 개념을 전제로 한다.

1. **인덱스**가 먼저인 이유: InnoDB의 모든 락은 인덱스 레코드에 걸린다. 인덱스 구조를 모르면 "왜 이 쿼리가 테이블 전체를 잠그는지"를 이해할 수 없다.
2. **트랜잭션**이 두 번째인 이유: 격리 수준(Isolation Level)이 락의 범위와 종류를 결정한다. REPEATABLE READ에서 Gap Lock이 왜 필요한지는 Phantom Read를 이해해야 설명된다.
3. **락**이 세 번째인 이유: 인덱스 + 트랜잭션 지식이 합쳐져야 Record Lock, Gap Lock, Next-Key Lock의 동작을 제대로 관찰할 수 있다.
4. **성능 개선**이 네 번째인 이유: 인덱스 설계와 락 경합을 이해한 상태에서 역정규화, 페이징, 파티셔닝의 트레이드오프를 판단할 수 있다.
5. **복제 지연**이 마지막인 이유: 단일 DB의 동작 원리가 모두 잡힌 후에야 Source-Replica 아키텍처의 실질적 제약을 체감할 수 있다.

---

## 모듈 의존 관계

```
module-index
    │
    ├──→ module-transaction
    │         │
    │         ├──→ module-lock ──→ module-performance
    │         │                        │
    │         └────────────────────────┘
    │
    └──────────────────────────────────→ module-replication
```

- module-lock은 module-index + module-transaction **모두** 선행 필요
- module-performance는 module-index 필수, module-lock 권장
- module-replication은 독립적이지만, 나머지를 마친 후 학습하는 게 효과적

---

## 모듈별 개요

| 순서 | 모듈 | 핵심 질문 | 예상 소요 |
|:---:|------|----------|:--------:|
| 1 | [module-index](./module-index/README.md) | InnoDB는 데이터를 어떻게 찾는가? | 3~4일 |
| 2 | [module-transaction](./module-transaction/README.md) | 동시에 실행되는 트랜잭션은 서로를 어떻게 격리하는가? | 2~3일 |
| 3 | [module-lock](./module-lock/README.md) | InnoDB는 동시성을 어떻게 제어하는가? | 4~5일 |
| 4 | [module-performance](./module-performance/README.md) | 대량 데이터에서 쿼리 성능을 어떻게 개선하는가? | 3~4일 |
| 5 | [module-replication](./module-replication/README.md) | 읽기 부하를 어떻게 분산하고, 복제 지연은 어떻게 다루는가? | 2~3일 |

총 예상: **2~3주** (하루 2~3시간 기준)

---

## 학습 원칙

### 1. 관찰 → 가설 → 실험 → 검증

단순히 코드를 실행하는 게 아니라, 매 실습마다 이 사이클을 반복한다.

```
예시:
관찰: "WHERE 절에 인덱스가 있는 컬럼을 쓰는데도 Full Table Scan이 발생한다"
가설: "조회 범위가 전체 데이터의 20% 이상이면 옵티마이저가 인덱스를 무시할 것이다"
실험: "데이터 100만건 중 1%, 10%, 20%, 50%를 조회하는 쿼리 각각에 EXPLAIN ANALYZE 실행"
검증: "실제로 약 15~25% 지점에서 index range scan → full table scan으로 전환됨을 확인"
```

### 2. EXPLAIN이 친구다

모든 쿼리에 습관적으로 `EXPLAIN ANALYZE`를 붙여본다. 특히 다음 컬럼에 주목:

- `type`: ALL(최악) → index → range → ref → eq_ref → const(최선)
- `rows`: 옵티마이저가 예측하는 스캔 행 수
- `Extra`: Using index(커버링), Using filesort, Using temporary

### 3. 테스트 코드가 학습 노트

각 테스트의 `@DisplayName`을 학습 내용 요약으로 쓴다. 나중에 테스트 목록만 봐도 복습이 된다.

```java
@DisplayName("REPEATABLE READ에서 범위 조건 UPDATE 시 Gap Lock이 걸려 다른 트랜잭션의 INSERT가 대기한다")
@Test
void gapLockPreventsInsertInRange() { ... }
```

### 4. MySQL 콘솔을 직접 쓴다

JPA/Spring만 통해서 보면 MySQL의 실제 동작이 추상화되어 보이지 않는다. 반드시 MySQL CLI나 DataGrip 등에서 직접 쿼리를 실행하며 관찰한다.

```bash
# Docker 컨테이너의 MySQL에 직접 접속
docker exec -it posty-mysql mysql -u root -proot posty
```

---

## 공통 도구 활용 가이드

### EXPLAIN 분석

```sql
-- 기본 실행 계획
EXPLAIN SELECT * FROM coupon WHERE issue_started_at < '2024-01-01';

-- 실제 실행 시간 포함 (MySQL 8.0.18+)
EXPLAIN ANALYZE SELECT * FROM coupon WHERE issue_started_at < '2024-01-01';
```

### 락 모니터링

```sql
-- 현재 걸린 락 확인 (MySQL 8.0+)
SELECT * FROM performance_schema.data_locks;

-- 락 대기 상황 확인
SELECT * FROM performance_schema.data_lock_waits;

-- InnoDB 엔진 상태 (데드락 로그 포함)
SHOW ENGINE INNODB STATUS;
```

### 트랜잭션 모니터링

```sql
-- 현재 활성 트랜잭션
SELECT * FROM information_schema.INNODB_TRX;

-- 프로세스 목록
SHOW PROCESSLIST;
```

### 쿼리 로깅 (p6spy)

`application.yml`에서 p6spy 설정 시 바인딩 파라미터까지 포함된 실제 쿼리를 확인할 수 있다. `hibernate.generate_statistics=true`와 함께 쓰면 영속성 컨텍스트의 flush/쿼리 횟수도 볼 수 있다.

---

## 면접 연결 포인트

각 모듈 학습 후 아래 질문에 자신 있게 답할 수 있는지 점검한다.

| 모듈 | 대표 면접 질문 |
|------|--------------|
| 인덱스 | "복합 인덱스의 컬럼 순서는 왜 중요한가요?" |
| 인덱스 | "커버링 인덱스란 무엇이고, 어떤 상황에서 효과적인가요?" |
| 트랜잭션 | "REPEATABLE READ에서 Phantom Read가 방지되는 원리는?" |
| 트랜잭션 | "Spring @Transactional의 propagation REQUIRES_NEW를 쓸 때 주의할 점은?" |
| 락 | "낙관적 락과 비관적 락의 차이, 각각 어떤 상황에서 선택하나요?" |
| 락 | "데드락이 발생하는 조건과 해결 방법은?" |
| 성능 | "Offset 페이징의 문제점과 대안은?" |
| 성능 | "역정규화를 결정하는 기준은 무엇인가요?" |
| 복제 | "복제 지연 상황에서 '쓰기 후 즉시 읽기' 문제를 어떻게 해결하나요?" |
