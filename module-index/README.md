# module-index: 인덱스 학습

> **핵심 질문**: InnoDB는 데이터를 어떻게 찾는가?

## 학습 목표

이 모듈을 마치면 다음을 설명할 수 있어야 한다:

- B+Tree 인덱스가 디스크 I/O를 줄이는 원리
- Clustered Index와 Secondary Index의 구조적 차이, Secondary Index 조회 시 왜 "테이블 액세스"가 한 번 더 발생하는지
- 복합 인덱스에서 컬럼 순서가 성능에 미치는 영향과 그 이유
- 옵티마이저가 인덱스를 사용하지 않기로 결정하는 조건들
- EXPLAIN 실행 계획의 각 항목이 의미하는 바

## 선행 지식

- SQL 기본 (SELECT, WHERE, JOIN, ORDER BY)
- JPA 엔티티 매핑 기본

---

## 학습 로드맵

### Step 1: InnoDB 저장 구조 이해 (개념)

- [x] **왜 인덱스가 필요한가**: Full Table Scan의 비용을 이해한다
  - 100만 건 테이블에서 `WHERE id = 500000` — 인덱스 없이 평균 50만 행을 읽어야 한다
  - 디스크 I/O 단위는 "페이지(16KB)"이며, 한 페이지에 수백 행이 들어간다

- [x] **B+Tree 구조**: 리프 노드에만 데이터(또는 PK 포인터)가 있고, 내부 노드는 탐색 경로만 제공한다
  - 깊이 3~4의 B+Tree로 수백만 건을 3~4번의 페이지 접근으로 찾을 수 있는 이유
- [x] **Clustered Index**: InnoDB에서 PK = 데이터 정렬 순서. 테이블 자체가 B+Tree다
  - PK를 지정하지 않으면? → InnoDB가 내부적으로 6바이트 Row ID를 만든다
- [x] **Secondary Index**: 리프 노드에 PK 값을 저장한다. 실제 데이터를 읽으려면 PK로 Clustered Index를 한 번 더 탐색한다 (이 과정을 "테이블 룩업" 또는 "클러스터드 
  인덱스 탐색"이라 한다)

**확인 질문**: "Secondary Index로 조회하면 항상 2번의 B+Tree 탐색이 발생하는가? 아닌 경우는?"
→ 커버링 인덱스일 때는 Secondary Index만으로 충분하다 (Step 5에서 실습)

### Step 2: EXPLAIN 읽는 법 익히기

- [x] 테스트 데이터 준비: Coupon 100만 건, Fruit 50만 건 삽입
- [x] `EXPLAIN`의 핵심 컬럼 이해
  - `type` 컬럼 (접근 방식): `const` > `eq_ref` > `ref` > `range` > `index` > `ALL`
  - `key`: 실제 사용된 인덱스
  - `rows`: 스캔 예상 행 수 (실제와 다를 수 있음 — 통계 기반 추정)
  - `Extra`: `Using index`(커버링), `Using where`(서버 필터링), `Using filesort`, `Using temporary`
- [x] `EXPLAIN ANALYZE`로 실제 실행 시간까지 확인

```sql
-- 실습: 같은 쿼리를 인덱스 유무로 비교
EXPLAIN ANALYZE SELECT * FROM coupon WHERE issue_started_at < '2024-01-01';
-- → type: ALL (인덱스 없을 때)

CREATE INDEX idx_coupon_issue_started_at ON coupon (issue_started_at);

EXPLAIN ANALYZE SELECT * FROM coupon WHERE issue_started_at < '2024-01-01';
-- → type: range (인덱스 있을 때)
```

### Step 3: 단일 컬럼 인덱스 실험

- [x] PK 조회 vs Secondary Index 조회 성능 비교
  - `WHERE id = ?` (const) vs `WHERE issue_started_at = ?` (ref)
- [x] 범위 조건에서 옵티마이저의 판단
  - `WHERE id < 10005` vs `WHERE id >= 999000` — 조회 범위 비율에 따라 인덱스 사용 여부가 달라진다
- [x] **인덱스가 무효화되는 케이스** 직접 확인
  - `WHERE SUBSTRING(name, 1, 1) = 'a'` → 함수 적용 시 인덱스 사용 불가
  - `WHERE name LIKE '%apple'` → 선행 와일드카드
  - `WHERE status != 'EXPIRED'` → NOT 조건
  - `WHERE price + 100 > 500` → 컬럼에 연산 적용
  - 각각에 EXPLAIN을 걸어 `type: ALL`이 되는 것을 직접 확인

**주의**: MySQL 8.0에서는 `함수 기반 인덱스`를 지원한다. `CREATE INDEX idx ON t ((SUBSTRING(name, 1, 1)))` 같은 게 가능하므로, "함수를 쓰면 무조건 안 된다"가 아니라 "일반 인덱스에서는 안 된다"로 이해할 것.

### Step 4: 복합 인덱스 (Composite Index)

- [x] 복합 인덱스의 "최좌선 접두사(Leftmost Prefix)" 규칙 실험

```sql
CREATE INDEX idx_status_started ON coupon (status, issue_started_at);

-- 인덱스 사용됨
EXPLAIN SELECT * FROM coupon WHERE status = 'ACTIVE';
EXPLAIN SELECT * FROM coupon WHERE status = 'ACTIVE' AND issue_started_at > '2024-01-01';

-- 인덱스 사용 안 됨
EXPLAIN SELECT * FROM coupon WHERE issue_started_at > '2024-01-01';
```

- [x] **컬럼 순서 결정 기준** 이해
  - 등호(=) 조건 컬럼을 앞에, 범위(>, <, BETWEEN) 조건 컬럼을 뒤에
  - 카디널리티가 높은 컬럼을 앞에 두는 게 일반적이지만, **쿼리 패턴**이 더 중요하다
  - 예: status(카디널리티 3)가 앞이고 issue_started_at(카디널리티 높음)이 뒤인 게 맞는 이유
- [x] 카디널리티 확인 방법

```sql
SHOW INDEX FROM coupon;  -- Cardinality 컬럼 확인
SELECT COUNT(DISTINCT status), COUNT(DISTINCT issue_started_at) FROM coupon;
```

### Step 5: 커버링 인덱스

- [x] 커버링 인덱스의 원리: SELECT하는 모든 컬럼이 인덱스에 포함되면 Clustered Index를 탐색할 필요가 없다
- [x] 실습 비교:

```sql
-- 커버링 인덱스 (Extra: Using index)
EXPLAIN SELECT name FROM fruit WHERE name LIKE 'ap%';

-- 비커버링 (테이블 룩업 발생)
EXPLAIN SELECT * FROM fruit WHERE name LIKE 'ap%';
```

- [x] 실행 시간 차이 측정: 50만 건 기준으로 얼마나 차이나는지 `EXPLAIN ANALYZE`로 확인
- [x] 실무에서의 트레이드오프: 커버링 인덱스를 위해 인덱스에 컬럼을 추가하면 쓰기 성능이 저하된다

### Step 6: 심화 — 옵티마이저 동작

- [ ] 인덱스 힌트 (`USE INDEX`, `FORCE INDEX`, `IGNORE INDEX`) 실험
- [ ] `ANALYZE TABLE`로 통계 갱신 후 실행 계획 변화 관찰
- [ ] Adaptive Hash Index: `SHOW ENGINE INNODB STATUS`에서 hash searches/s 확인

---

## 검증 체크리스트

학습을 마친 후 아래 질문에 코드/쿼리 결과로 답할 수 있는지 확인한다.

- [ ] "Clustered Index와 Secondary Index의 리프 노드에 각각 무엇이 저장되는가?"
- [ ] "복합 인덱스 (A, B, C)에서 `WHERE B = 1 AND C = 2`는 왜 인덱스를 활용하지 못하는가?"
- [ ] "100만 건 중 80%를 조회하는 range 쿼리에서 옵티마이저가 인덱스를 안 쓰는 이유는?"
- [ ] "커버링 인덱스가 적용된 쿼리와 아닌 쿼리의 EXPLAIN 결과 차이는?"

---

## 학습 자료

- [MySQL 8.0 공식 문서 — InnoDB Index](https://dev.mysql.com/doc/refman/8.0/en/innodb-index-types.html)
- [MySQL 8.0 공식 문서 — EXPLAIN Output Format](https://dev.mysql.com/doc/refman/8.0/en/explain-output.html)
- [Real MySQL 8.0 (위키북스)](https://product.kyobobook.co.kr/detail/S000001766482) — 8장 인덱스
- [USE THE INDEX, LUKE](https://use-the-index-luke.com/) — SQL 인덱싱 전문 사이트 (무료, 영문)
- [우아한테크코스 MySQL 인덱스 강의 자료] — 과정 내부 자료 참고

---

## 다음 모듈 연결

인덱스를 마쳤다면 **module-transaction**으로 넘어간다. 트랜잭션 격리 수준을 학습할 때 "인덱스 레코드에 락이 걸린다"는 사실을 기억하면 Gap Lock의 동작이 훨씬 자연스럽게 이해된다.
