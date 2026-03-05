-- ============================================
-- module-index 학습용 테스트 데이터 생성 스크립트
-- Coupon: 100만 건 / Fruit: 50만 건
-- ============================================
--
-- 실행 방법:
--   mysql -u root -proot posty < init-test-data.sql
--
-- 소요 시간: 약 1~3분 (환경에 따라 다름)
-- ============================================

SET autocommit = 0;
SET foreign_key_checks = 0;
SET unique_checks = 0;

-- 테이블 생성 (JPA ddl-auto 없이 독립 실행 시)
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    discount_amount INT,
    quantity INT,
    issue_started_at DATETIME(6),
    issue_ended_at DATETIME(6),
    status VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS fruit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price INT,
    category VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6)
) ENGINE=InnoDB;

-- 기존 데이터 초기화
TRUNCATE TABLE coupon;
TRUNCATE TABLE fruit;

-- 헬퍼: 숫자 테이블 (1~1000)
DROP TABLE IF EXISTS _nums;
CREATE TABLE _nums (n INT PRIMARY KEY);
INSERT INTO _nums (n)
WITH RECURSIVE cte AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM cte WHERE n < 1000
)
SELECT n FROM cte;

-- ============================================
-- Coupon 100만 건 생성
-- ============================================
-- status 분포 (편향): EXPIRED 60% / ACTIVE 25% / UPCOMING 15%
--   → 옵티마이저가 status 값에 따라 인덱스 사용 여부를 다르게 판단하는 것을 관찰 가능
-- issue_started_at  : 2023-01-01 ~ 2024-12-31 (소수 승수로 비균등 분산)
-- discount_amount   : 100 ~ 5,000 (비순차 분산)
-- quantity          : 1 ~ 500
-- name 접두어       : welcome / seasonal / vip / event / flash (5종, 비순차)

INSERT INTO coupon (name, discount_amount, quantity, issue_started_at, issue_ended_at, status, created_at, updated_at)
SELECT
    CONCAT(
        ELT(1 + ((row_num * 7) % 5), 'welcome', 'seasonal', 'vip', 'event', 'flash'),
        '_coupon_', row_num
    ),
    (1 + ((row_num * 13) % 50)) * 100,
    1 + ((row_num * 31) % 500),
    DATE_ADD('2023-01-01', INTERVAL ((row_num * 37) % 730) DAY),
    DATE_ADD('2023-01-01', INTERVAL ((row_num * 37) % 730) + 7 + ((row_num * 11) % 54) DAY),
    CASE
        WHEN row_num % 100 < 60 THEN 'EXPIRED'
        WHEN row_num % 100 < 85 THEN 'ACTIVE'
        ELSE 'UPCOMING'
    END,
    DATE_SUB(NOW(), INTERVAL ((row_num * 17) % 365) DAY),
    NOW()
FROM (
    SELECT (a.n - 1) * 1000 + b.n AS row_num
    FROM _nums a CROSS JOIN _nums b
) t;

COMMIT;

-- ============================================
-- Fruit 50만 건 생성
-- ============================================
-- category 분포 (편향): CITRUS 40% / TROPICAL 25% / BERRY 15% / STONE_FRUIT 12% / OTHER 8%
--   → 편향된 카테고리로 옵티마이저의 선택적 인덱스 사용을 관찰 가능
-- name  : 20종 과일명 + 번호 (소수 승수로 비순차 분산)
-- price : 100 ~ 10,050 (비순차 분산)

INSERT INTO fruit (name, price, category, created_at, updated_at)
SELECT
    CONCAT(
        ELT(1 + ((row_num * 7) % 20),
            'apple', 'banana', 'cherry', 'grape', 'kiwi',
            'lemon', 'mango', 'orange', 'peach', 'pear',
            'plum', 'melon', 'berry', 'fig', 'lime',
            'papaya', 'guava', 'lychee', 'coconut', 'apricot'),
        '_', row_num
    ),
    100 + ((row_num * 29) % 200) * 50,
    CASE
        WHEN row_num % 100 < 40 THEN 'CITRUS'
        WHEN row_num % 100 < 65 THEN 'TROPICAL'
        WHEN row_num % 100 < 80 THEN 'BERRY'
        WHEN row_num % 100 < 92 THEN 'STONE_FRUIT'
        ELSE 'OTHER'
    END,
    DATE_SUB(NOW(), INTERVAL ((row_num * 19) % 365) DAY),
    NOW()
FROM (
    SELECT (a.n - 1) * 500 + b.n AS row_num
    FROM _nums a CROSS JOIN (SELECT n FROM _nums WHERE n <= 500) b
) t;

COMMIT;

-- 헬퍼 테이블 정리
DROP TABLE IF EXISTS _nums;

-- 설정 복원
SET autocommit = 1;
SET foreign_key_checks = 1;
SET unique_checks = 1;

-- 결과 확인
SELECT 'coupon' AS table_name, COUNT(*) AS row_count FROM coupon
UNION ALL
SELECT 'fruit', COUNT(*) FROM fruit;
