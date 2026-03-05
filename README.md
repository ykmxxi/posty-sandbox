# posty-sandbox

우아한테크코스 레벨4 MySQL 고급 학습 프로젝트.
Gradle 멀티모듈 구조로 **인덱스, 트랜잭션, 락, 성능 개선, 복제 지연** 5개 주제를 독립적으로 학습합니다.

## 학습 순서

```
[1] 인덱스 → [2] 트랜잭션 → [3] 락 → [4] 성능 개선 → [5] 복제 지연
```

| 순서 | 모듈 | 핵심 질문 |
|:---:|------|----------|
| 1 | [module-index](module-index/) | InnoDB는 데이터를 어떻게 찾는가? |
| 2 | [module-transaction](module-transaction/) | 동시 트랜잭션은 서로를 어떻게 격리하는가? |
| 3 | [module-lock](module-lock/) | InnoDB는 동시성을 어떻게 제어하는가? |
| 4 | [module-performance](module-performance/) | 대량 데이터에서 쿼리 성능을 어떻게 개선하는가? |
| 5 | [module-replication](module-replication/) | 읽기 부하를 어떻게 분산하고 복제 지연은 어떻게 다루는가? |

> 각 모듈의 README.md에 상세 학습 로드맵이 있습니다.

## 프로젝트 구조

```
posty-sandbox/
├── common/                  # 공통 모듈 (BaseEntity, P6spyConfig, JpaAuditingConfig)
├── module-index/            # [1] 인덱스 학습
├── module-transaction/      # [2] 트랜잭션 학습
├── module-lock/             # [3] 락 학습
├── module-performance/      # [4] 성능 개선 학습
├── module-replication/      # [5] 복제 지연 학습
├── docker/
│   ├── mysql/               # init.sql (참고용)
│   └── mysql-replication/   # Source-Replica 설정 파일 (my.cnf, scripts)
├── jpa-learning/            # (기존) JPA 학습
├── jpa-inheritance/         # (기존) JPA 상속 학습
└── mvc/                     # (기존) MVC 학습
```

## 기술 스택

- Java 21, Spring Boot 3.4.4, Spring Data JPA (Hibernate 6.x)
- MySQL 8.0, Docker Compose, Testcontainers
- p6spy (쿼리 로깅), JUnit 5

## Docker 실행 방법

루트 `docker-compose.yml` 하나로 모든 인프라를 관리합니다. **profile**로 환경을 선택합니다.

### 단일 MySQL (모듈 1~4: 인덱스, 트랜잭션, 락, 성능 개선)

```bash
docker compose up -d

# MySQL 접속
docker exec -it posty-sandbox-mysql mysql -uroot -proot posty

# 종료
docker compose down
```

### Source-Replica 복제 환경 (모듈 5: 복제 지연)

```bash
docker compose --profile replication up -d

# Source 접속 (포트 3307)
docker exec -it posty-sandbox-mysql-source mysql -uroot -proot posty

# Replica 접속 (포트 3308)
docker exec -it posty-sandbox-mysql-replica mysql -uroot -proot posty

# 복제 상태 확인
docker exec -it posty-sandbox-mysql-replica mysql -uroot -proot -e "SHOW REPLICA STATUS\G"

# 종료
docker compose --profile replication down
```

## 테스트 실행

각 모듈의 테스트는 Testcontainers로 MySQL 컨테이너를 자동 기동합니다. Docker만 실행 중이면 됩니다.

```bash
# 특정 모듈 테스트
./gradlew :module-index:test

# 전체 빌드 (테스트 제외)
./gradlew clean build -x test
```

## 스키마 관리

- Flyway 미사용
- `ddl-auto=create-drop` + `@Sql` 어노테이션으로 테스트마다 깨끗한 상태에서 시작
- 파티셔닝 등 특수 DDL은 `@Sql` 스크립트로 직접 실행
