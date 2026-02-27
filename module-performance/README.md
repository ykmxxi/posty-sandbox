# module-performance: 성능 개선 학습

> **핵심 질문**: 대량 데이터에서 쿼리 성능을 어떻게 개선하는가?

## 학습 목표

이 모듈을 마치면 다음을 설명할 수 있어야 한다:

- 역정규화를 결정하는 기준과 데이터 정합성 유지 전략
- Offset 페이징이 느려지는 원리와 Cursor 기반 페이징의 동작 방식
- 파티셔닝이 효과적인 경우와 그렇지 않은 경우
- 쿼리 성능을 정량적으로 측정하고 비교하는 방법

## 선행 지식

- **module-index 필수**: 인덱스 동작 원리, EXPLAIN 읽는 법
- **module-lock 권장**: 인덱스 추가가 쓰기 성능에 미치는 영향 이해

---

## 학습 로드맵

### Step 0: 대량 데이터 준비

모든 성능 실험은 **충분한 데이터**가 있어야 의미 있다. 100건에서는 차이가 안 난다.

- [ ] Coupon 100만 건, Post 50만 건, UserOrder 100만 건 데이터 삽입
- [ ] 삽입 방법 비교 (이것 자체가 학습):

```java
// 방법 1: JPA saveAll — 느리다 (건당 INSERT, auto-increment 조회)
couponRepository.saveAll(coupons);  // 100만건 → 수 분 소요

// 방법 2: JDBC Batch Insert — 빠르다
jdbcTemplate.batchUpdate(
    "INSERT INTO coupon (name, quantity, status) VALUES (?, ?, ?)",
    new BatchPreparedStatementSetter() { ... }
);

// 방법 3: MySQL LOAD DATA INFILE — 가장 빠르다
LOAD DATA INFILE '/tmp/coupons.csv' INTO TABLE coupon ...;
```

- [ ] 각 방법의 소요 시간을 측정하고 **왜** 차이가 나는지 이해
  - JPA: 엔티티 영속화 → dirty checking → 개별 INSERT
  - JDBC Batch: 다수의 INSERT를 하나의 네트워크 round-trip으로
  - LOAD DATA: MySQL이 직접 파일을 읽어 bulk load (로깅, 인덱스 업데이트 최소화)

---

### Step 1: 역정규화 (Denormalization)

#### 1-1. 문제 인식

- [ ] "특정 유저의 게시글 수"를 조회하는 2가지 방법 비교:

```sql
-- 정규화 방식: 매번 COUNT
SELECT COUNT(*) FROM post WHERE user_id = 123;
-- 50만 건 테이블, 인덱스 있어도 수백 ms

-- 역정규화 방식: 미리 집계된 값 조회
SELECT post_count FROM post_statistics WHERE user_id = 123;
-- 단일 행 조회, < 1ms
```

- [ ] `EXPLAIN ANALYZE`로 두 쿼리의 실행 시간과 스캔 행 수를 정량 비교

#### 1-2. 역정규화 구현

- [ ] `PostStatistics` 테이블 설계: `user_id (UNIQUE)`, `post_count`
- [ ] 게시글 INSERT/DELETE 시 `post_count`를 함께 갱신하는 로직 구현
- [ ] **정합성 유지 전략** 비교:
  - 동기 방식: 같은 트랜잭션에서 `post_count += 1` → 단순하지만 쓰기 성능 저하
  - 비동기 방식: 이벤트 발행 → 별도 트랜잭션에서 갱신 → 일시적 불일치 허용
  - 배치 방식: 주기적으로 `UPDATE post_statistics SET post_count = (SELECT COUNT(*) ...)` → 실시간성 포기

**핵심 포인트**: 역정규화는 "읽기 성능 vs 쓰기 복잡성 + 정합성 리스크"의 트레이드오프다. Forgather에서 "이벤트 앨범의 사진 수"를 표시하는 기능에도 같은 판단이 필요하다.

#### 1-3. 역정규화 결정 기준

- [ ] 역정규화가 적합한 조건 정리:
  - 읽기 빈도가 쓰기 빈도보다 **압도적으로** 높을 때
  - 집계 쿼리의 비용이 높을 때 (대량 데이터, GROUP BY, JOIN)
  - 일시적 데이터 불일치가 허용될 때
- [ ] 역정규화 없이도 해결 가능한 대안 검토:
  - 캐시 (Redis): 정합성 관리가 역정규화보다 유연
  - Materialized View: MySQL은 미지원, 하지만 개념은 동일
  - 인덱스 최적화로 COUNT 자체를 빠르게

---

### Step 2: 페이징 성능

#### 2-1. Offset 페이징의 문제

- [ ] 실험: 100만 건에서 Offset을 점점 키워가며 실행 시간 측정

```sql
SELECT * FROM coupon ORDER BY id LIMIT 10 OFFSET 0;       -- ~1ms
SELECT * FROM coupon ORDER BY id LIMIT 10 OFFSET 10000;   -- ~10ms
SELECT * FROM coupon ORDER BY id LIMIT 10 OFFSET 100000;  -- ~100ms
SELECT * FROM coupon ORDER BY id LIMIT 10 OFFSET 500000;  -- ~500ms+
```

- [ ] **왜 느린가**: `OFFSET N`은 N개의 행을 읽고 버린다. Offset이 커질수록 버리는 행이 많아진다.
- [ ] `EXPLAIN`으로 `rows` 값 확인: OFFSET 500000이면 500010개를 스캔한다

#### 2-2. Cursor 기반 페이징 (Keyset Pagination)

- [ ] 마지막으로 조회한 행의 PK(또는 정렬 키)를 기준으로 다음 페이지 조회:

```sql
-- 첫 페이지
SELECT * FROM coupon ORDER BY id LIMIT 10;

-- 다음 페이지 (이전 페이지의 마지막 id가 100이었다면)
SELECT * FROM coupon WHERE id > 100 ORDER BY id LIMIT 10;
-- → 항상 인덱스 range scan, OFFSET과 무관하게 일정한 성능
```

- [ ] 50만 번째 페이지에서도 성능이 일정한 것을 측정으로 확인
- [ ] **Cursor 페이징의 제약**:
  - 정렬 키가 유일해야 한다 (또는 유일한 조합이어야 한다)
  - "N번째 페이지로 바로 이동"이 불가능하다 → 무한 스크롤 UI에 적합
  - 정렬 기준이 변경되면 구현이 복잡해진다

#### 2-3. Covering Index를 활용한 Offset 개선

- [ ] Offset을 완전히 버릴 수 없는 경우 (페이지 번호 UI가 필요할 때), 서브쿼리로 PK만 먼저 추출:

```sql
-- Deferred Join 패턴
SELECT c.*
FROM coupon c
JOIN (SELECT id FROM coupon ORDER BY id LIMIT 10 OFFSET 500000) sub
ON c.id = sub.id;
-- 서브쿼리는 커버링 인덱스로 빠르게 실행, 본 쿼리는 10건만 조회
```

- [ ] 일반 OFFSET vs Deferred Join 성능 비교

---

### Step 3: 파티셔닝

#### 3-1. RANGE 파티셔닝 실습

- [ ] 주문 테이블을 월별로 파티셔닝:

```sql
CREATE TABLE user_order_partitioned (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    total_amount INT,
    ordered_at DATETIME,
    PRIMARY KEY (id, ordered_at)  -- 파티션 키가 PK에 포함되어야 함
) PARTITION BY RANGE (YEAR(ordered_at) * 100 + MONTH(ordered_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    ...
);
```

- [ ] 파티셔닝 효과 확인:

```sql
-- 파티션 프루닝: 특정 월만 스캔
EXPLAIN SELECT * FROM user_order_partitioned
WHERE ordered_at BETWEEN '2024-01-01' AND '2024-01-31';
-- partitions: p202401 (해당 파티션만 접근)
```

#### 3-2. 파티셔닝의 트레이드오프

- [ ] **효과적인 경우**: 시계열 데이터, 오래된 데이터 삭제(`ALTER TABLE DROP PARTITION`이 DELETE보다 빠름), 특정 범위 쿼리가 주된 접근 패턴
- [ ] **비효과적인 경우**: 파티션 키가 아닌 컬럼으로 조회 → 모든 파티션 스캔, UNIQUE 제약조건에 파티션 키가 반드시 포함되어야 하는 제약
- [ ] **샤딩과의 차이** (개념): 파티셔닝은 단일 서버 내 테이블 분할, 샤딩은 여러 서버로 데이터 분산

---

### Step 4: 성능 측정 방법론

- [ ] **쿼리 단위 측정**: `EXPLAIN ANALYZE`의 actual time
- [ ] **애플리케이션 단위 측정**: Spring의 `StopWatch` 또는 `System.nanoTime()`
- [ ] **반복 측정의 중요성**: 첫 실행은 디스크 I/O(cold cache), 이후는 buffer pool에서 읽기(warm cache). 최소 3회 실행 후 중앙값 사용
- [ ] **MySQL Slow Query Log 설정**:

```sql
SET GLOBAL slow_query_log = 1;
SET GLOBAL long_query_time = 0.1;  -- 100ms 이상 쿼리 기록
SET GLOBAL log_queries_not_using_indexes = 1;  -- 인덱스 미사용 쿼리도 기록
```

- [ ] `Performance Schema`를 통한 쿼리별 통계 확인:

```sql
SELECT DIGEST_TEXT, COUNT_STAR, AVG_TIMER_WAIT/1000000000 as avg_ms
FROM performance_schema.events_statements_summary_by_digest
ORDER BY AVG_TIMER_WAIT DESC
LIMIT 10;
```

---

## 검증 체크리스트

- [ ] "역정규화 테이블의 데이터 정합성을 보장하는 3가지 전략과 각각의 트레이드오프는?"
- [ ] "OFFSET 500000이 느린 이유를 B+Tree 구조 관점에서 설명하라"
- [ ] "Cursor 페이징이 항상 Offset보다 좋은가? 그렇지 않은 경우는?"
- [ ] "파티셔닝을 적용했는데 오히려 느려지는 경우는?"
- [ ] "100만 건 INSERT 시 JPA saveAll vs JDBC Batch의 성능 차이가 나는 이유는?"

---

## 학습 자료

- [MySQL 8.0 공식 문서 — Partitioning](https://dev.mysql.com/doc/refman/8.0/en/partitioning.html)
- [MySQL 8.0 공식 문서 — EXPLAIN ANALYZE](https://dev.mysql.com/doc/refman/8.0/en/explain.html)
- [Real MySQL 8.0 (위키북스)](https://product.kyobobook.co.kr/detail/S000001766482) — 10장 실행 계획, 15장 데이터 타입
- [High Performance MySQL (O'Reilly)](https://www.oreilly.com/library/view/high-performance-mysql/9781492080503/) — 파티셔닝, 쿼리 최적화 챕터
- [우아한형제들 기술블로그 — 페이징 성능 개선](https://techblog.woowahan.com/) — "1억 건의 데이터에서 페이징"

---

## 다음 모듈 연결

단일 DB의 성능 최적화를 마쳤다면 **module-replication**으로 넘어간다. "단일 DB로 감당할 수 없는 읽기 부하는 어떻게 분산하는가?"가 자연스러운 다음 질문이다. 또한 역정규화의 비동기 갱신 패턴은 복제 환경에서의 "eventual consistency"와 본질적으로 같은 문제를 다룬다.
