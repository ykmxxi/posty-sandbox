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

- [x] 인덱스 힌트 (`USE INDEX`, `FORCE INDEX`, `IGNORE INDEX`) 실험
- [x] `ANALYZE TABLE`로 통계 갱신 후 실행 계획 변화 관찰
- [x] Adaptive Hash Index: `SHOW ENGINE INNODB STATUS`에서 hash searches/s 확인

---

## 검증 체크리스트

학습을 마친 후 아래 질문에 코드/쿼리 결과로 답할 수 있는지 확인한다.

- "Clustered Index와 Secondary Index의 리프 노드에 각각 무엇이 저장되는가?"
> Clustered Index는 테이블이 생성되면 PK를 이용해 B+Tree 인덱스 구조로 데이터 테이블을 만든다.
> 클러스터 인덱스의 리프 노드에는 PK와 해당 row의 데이터(전체 컬럼)가 모두 존재한다. 즉, 테이블 자체가 클러스터드 인덱스다.
> Secondary Index는 별도의 B+Tree 인덱스 구조를 만든다.
> 비클러스터 인덱스의 리프 노드에는 **인덱스 컬럼 값 + PK**가 존재한다. (PK"만" 있는 게 아니라 인덱스로 지정한 컬럼 값도 함께 저장된다)
>
> 📌 PK가 없는 테이블의 경우: 첫 번째 UNIQUE NOT NULL 인덱스를 클러스터드 인덱스로 사용하고,
> 그것도 없으면 InnoDB가 내부적으로 6바이트 hidden Row ID를 생성해 클러스터드 인덱스를 만든다.
>
> 📖 비유: 클러스터드 인덱스는 "사전" 자체다 — 단어(PK) 순서대로 정렬되어 있고 뜻(데이터)이 바로 옆에 있다.
> Secondary Index는 "사전 뒤의 색인(찾아보기)"이다 — 키워드(인덱스 컬럼)와 페이지 번호(PK)가 적혀 있고,
> 실제 내용을 보려면 해당 페이지(클러스터드 인덱스)로 넘어가야 한다.

- "복합 인덱스 (A, B, C)에서 `WHERE B = 1 AND C = 2`는 왜 인덱스를 활용하지 못하는가?"
> 복합 인덱스는 최좌측 접두사 규칙(Leftmost Prefix Rule)이 적용된다.
> 복합 인덱스 (A, B, C)는 **A로 먼저 정렬 → A가 같은 값 내에서 B로 정렬 → B가 같은 값 내에서 C로 정렬**하는 구조이다.
> A가 결정되지 않은 상태에서 B, C만으로 조회하면, B의 값이 인덱스 전체에 걸쳐 흩어져 있어 탐색 범위를 좁힐 수 없다.
>
> 📖 비유: 전화번호부가 "성(A) → 이름(B) → 생년(C)" 순서로 정렬되어 있다고 하자.
> 성을 모른 채 이름이 "민수"이고 생년이 "1995"인 사람을 찾으려면,
> 전화번호부 전체를 처음부터 끝까지 넘기면서 확인해야 한다 — 정렬이 도움이 되지 않는다.
>
> ⚠️ 예외: MySQL 8.0+에서는 **Index Skip Scan** 최적화가 존재한다.
> 선행 컬럼(A)의 카디널리티가 낮으면 A의 모든 distinct 값에 대해 각각 (A=val, B=1, C=2)로
> 탐색하는 방식으로 인덱스를 활용할 수 있다. `EXPLAIN`에서 `Using index for skip scan`으로 확인 가능.

- "100만 건 중 80%를 조회하는 range 쿼리에서 옵티마이저가 인덱스를 안 쓰는 이유는?"
> Secondary Index의 리프 노드는 인덱스 컬럼 기준으로 정렬되어 있지만,
> 거기서 가리키는 실제 데이터 페이지(클러스터드 인덱스)는 PK 순서로 배치되어 있다.
> 인덱스 순서 ≠ 데이터 페이지의 물리적 순서이기 때문에, 건건이 다른 페이지를 읽어야 하는 **Random I/O**가 발생한다.
>
> 전체 데이터의 80%를 조회하면 거의 모든 데이터 페이지를 Random I/O로 접근해야 하므로 비용이 매우 크다.
> 반면 Full Table Scan은 클러스터드 인덱스의 리프 노드를 처음부터 끝까지 순차적으로 읽는 **Sequential I/O**이다.
> 옵티마이저는 이 비용을 비교해 Full Table Scan이 더 효율적이라 판단하고 인덱스를 사용하지 않는다.
>
> 📖 비유: 도서관에서 책 80%를 찾아야 한다고 하자.
> 색인(인덱스)을 보고 한 권씩 서가 이곳저곳을 오가며 찾는 것(Random I/O)보다,
> 서가를 처음부터 끝까지 순서대로 훑으며 전부 꺼내는 게(Sequential I/O) 훨씬 빠르다.
>
> 📌 옵티마이저의 판단 기준: 일반적으로 전체 데이터의 **약 20~25% 이상**을 읽어야 하면
> Full Table Scan이 유리하다고 판단한다. 다만 이 임계값은 테이블 크기, 버퍼 풀 캐시 상태,
> 데이터 분포에 따라 달라지므로 절대적인 수치는 아니다.

- "커버링 인덱스가 적용된 쿼리와 아닌 쿼리의 EXPLAIN 결과 차이는?"
> 커버링 인덱스가 적용된 쿼리는 SELECT에 필요한 모든 컬럼이 인덱스에 포함되어 있으므로
> 클러스터드 인덱스로의 2차 조회(Double Lookup)가 발생하지 않는다.
> 반대로, 커버링 인덱스가 적용되지 않으면 인덱스를 사용하더라도 리프 노드의 PK를 이용해 클러스터드 인덱스를 다시 탐색하는 Double Lookup이 필요하다.
>
> EXPLAIN의 Extra 컬럼에서 차이를 확인할 수 있다:
>
> | Extra 값                | 의미                                                    |
> |------------------------|--------------------------------------------------------|
> | `Using index`          | ✅ 커버링 인덱스 적용. 인덱스만으로 결과 반환, 데이터 페이지 접근 불필요 |
> | `Using where`          | 스토리지 엔진에서 가져온 후 MySQL 서버 레벨에서 추가 필터링              |
> | `Using index condition`| ICP(Index Condition Pushdown) 적용. 인덱스 레벨에서 조건 필터링 후 데이터 페이지 접근 |
>
> ⚠️ 주의: `Using index condition`은 커버링 인덱스가 **아니다**.
> ICP는 인덱스 레벨에서 가능한 조건을 먼저 걸러내는 최적화이지만, 최종적으로 데이터 페이지 접근이 필요하다.
> `Using index`와 혼동하지 않도록 주의할 것.

- "PK를 AUTO_INCREMENT BIGINT vs UUID로 설정했을 때 인덱스 성능 차이는?"
> 클러스터드 인덱스는 PK 순서대로 데이터를 물리적으로 배치한다.
> AUTO_INCREMENT는 항상 증가하는 값이므로 새 행이 B+Tree의 맨 끝에 순차적으로 추가된다 → 페이지 분할(Page Split)이 거의 발생하지 않는다.
> 반면 UUID는 랜덤한 값이므로 새 행이 B+Tree의 중간 어딘가에 삽입된다 → 기존 페이지가 가득 찬 경우 페이지 분할이 빈번하게 발생한다.
> 페이지 분할은 기존 페이지의 데이터를 반으로 나눠 새 페이지로 이동시키는 비용이 큰 작업이므로 INSERT 성능이 크게 저하된다.
> 또한 UUID(36바이트)는 BIGINT(8바이트)보다 크기가 크므로, 모든 Secondary Index의 리프 노드에도 더 큰 PK가 저장되어 인덱스 크기가 증가한다.
>
> 📖 비유: AUTO_INCREMENT는 도서관 책장 맨 끝에 새 책을 꽂는 것이다 — 빠르고 간단하다.
> UUID는 이미 빽빽한 책장 중간에 책을 끼워 넣는 것이다 — 자리를 만들기 위해 기존 책을 옮겨야 한다.

- "인덱스를 추가하면 SELECT는 빨라지지만 INSERT/UPDATE/DELETE는 왜 느려지는가?"
> 인덱스는 별도의 B+Tree 자료구조이다. 테이블에 인덱스가 N개 있으면,
> 데이터를 INSERT할 때 클러스터드 인덱스 + N개의 Secondary Index B+Tree를 모두 갱신해야 한다.
> UPDATE는 변경된 컬럼이 포함된 인덱스를, DELETE는 모든 인덱스에서 해당 항목을 제거해야 한다.
> 따라서 인덱스 개수에 비례하는 쓰기 오버헤드가 발생한다.
>
> 📌 실무 설계 기준: 읽기/쓰기 비율을 고려해 인덱스를 설계해야 한다.
> 읽기가 압도적으로 많은 서비스(예: 블로그, 커머스 상품 조회)는 인덱스를 적극 활용하고,
> 쓰기가 많은 서비스(예: 로그 수집, IoT 센서 데이터)는 인덱스를 최소화하는 전략이 필요하다.
> 핵심 조회 패턴에만 인덱스를 걸고, 불필요한 인덱스는 과감히 제거하는 균형이 중요하다.

---

## 테스트 데이터 준비

학습에 필요한 대용량 테스트 데이터(Coupon 100만 건, Fruit 50만 건)를 생성하고 DB에 적재하는 방법이다.

### 사전 준비

- Python 3.x
- Docker (MySQL 컨테이너 실행 중)

### 실행 방법

`data/load-data.sh` 스크립트 하나로 venv 생성, CSV 생성, DB 적재, 검증까지 자동으로 수행된다.

```bash
cd module-index/data
chmod +x load-data.sh
./load-data.sh
```

### 스크립트 동작 순서

1. Python venv 생성 및 numpy 설치
2. `generate_test_data.py` 실행 → `coupon.csv`, `fruit.csv` 생성
3. Docker 컨테이너로 CSV 복사
4. `LOAD DATA INFILE`로 DB에 적재
5. `verify-distributions.sql`로 데이터 분포 검증
6. 컨테이너 내 임시 CSV 파일 정리

### 파일 구성

| 파일 | 설명 |
|------|------|
| `generate_test_data.py` | CSV 데이터 생성 스크립트 (시드 고정, 재현 가능) |
| `load-data.sh` | 전체 적재 자동화 스크립트 |
| `verify-distributions.sql` | 적재 후 데이터 분포 검증 쿼리 |
| `init-test-data-legacy.sql` | 레거시 데이터 초기화 SQL |

> **참고**: `.csv` 파일과 `.venv/` 디렉토리는 `.gitignore`에 의해 제외된다. 스크립트 실행 시 자동으로 생성되므로 별도로 관리할 필요 없다.

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
