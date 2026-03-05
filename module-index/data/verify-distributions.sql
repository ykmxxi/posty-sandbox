-- ============================================
-- 테스트 데이터 분포 검증 쿼리
-- ============================================

-- 1. 총 건수 확인
SELECT '=== 1. 총 건수 ===' AS section;
SELECT 'coupon' AS table_name, COUNT(*) AS row_count FROM coupon
UNION ALL
SELECT 'fruit', COUNT(*) FROM fruit;

-- 2. Coupon status 분포 (목표: EXPIRED ~60%, ACTIVE ~25%, UPCOMING ~15%)
SELECT '=== 2. Coupon status 분포 ===' AS section;
SELECT
    status,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM coupon), 1) AS pct
FROM coupon
GROUP BY status
ORDER BY cnt DESC;

-- 3. Fruit category 분포 (목표: CITRUS 40%, TROPICAL 25%, BERRY 15%, STONE_FRUIT 12%, OTHER 8%)
SELECT '=== 3. Fruit category 분포 ===' AS section;
SELECT
    category,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM fruit), 1) AS pct
FROM fruit
GROUP BY category
ORDER BY cnt DESC;

-- 4. discount_amount 앵커 비율 (목표: 1000/3000/5000 합계 ~70%)
SELECT '=== 4. discount_amount 앵커 비율 ===' AS section;
SELECT
    CASE
        WHEN discount_amount IN (1000, 3000, 5000) THEN CAST(discount_amount AS CHAR)
        ELSE 'other'
    END AS amount_group,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM coupon), 1) AS pct
FROM coupon
GROUP BY amount_group
ORDER BY cnt DESC;

-- 5. price 구간별 분포 (목표: 1000~3000 = ~50%)
SELECT '=== 5. Fruit price 구간별 분포 ===' AS section;
SELECT
    CASE
        WHEN price < 1000 THEN '< 1000'
        WHEN price BETWEEN 1000 AND 2000 THEN '1000-2000'
        WHEN price BETWEEN 2001 AND 3000 THEN '2001-3000'
        WHEN price BETWEEN 3001 AND 5000 THEN '3001-5000'
        ELSE '> 5000'
    END AS price_range,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM fruit), 1) AS pct
FROM fruit
GROUP BY price_range
ORDER BY
    CASE price_range
        WHEN '< 1000' THEN 1
        WHEN '1000-2000' THEN 2
        WHEN '2001-3000' THEN 3
        WHEN '3001-5000' THEN 4
        ELSE 5
    END;

-- 6. 날짜 recency (목표: 최근 6개월 = ~60%)
SELECT '=== 6. Coupon issue_started_at recency ===' AS section;
SELECT
    CASE
        WHEN issue_started_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) THEN '최근 6개월'
        WHEN issue_started_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH) THEN '6~12개월'
        ELSE '1년 이상'
    END AS period,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM coupon), 1) AS pct
FROM coupon
GROUP BY period
ORDER BY
    CASE period
        WHEN '최근 6개월' THEN 1
        WHEN '6~12개월' THEN 2
        ELSE 3
    END;

-- 7. status-날짜 상관관계 (오래된 쿠폰일수록 EXPIRED 비율 높아야 함)
SELECT '=== 7. status-날짜 상관관계 ===' AS section;
SELECT
    CASE
        WHEN DATEDIFF(NOW(), issue_started_at) > 365 THEN 'a) > 1년'
        WHEN DATEDIFF(NOW(), issue_started_at) > 180 THEN 'b) 6개월~1년'
        WHEN DATEDIFF(NOW(), issue_started_at) > 30  THEN 'c) 1~6개월'
        ELSE 'd) < 1개월'
    END AS age_group,
    status,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (PARTITION BY
        CASE
            WHEN DATEDIFF(NOW(), issue_started_at) > 365 THEN 'a) > 1년'
            WHEN DATEDIFF(NOW(), issue_started_at) > 180 THEN 'b) 6개월~1년'
            WHEN DATEDIFF(NOW(), issue_started_at) > 30  THEN 'c) 1~6개월'
            ELSE 'd) < 1개월'
        END
    ), 1) AS pct_in_group
FROM coupon
GROUP BY age_group, status
ORDER BY age_group, status;

-- 8. category-가격 상관관계 (TROPICAL 평균가 > BERRY 평균가)
SELECT '=== 8. category-가격 상관관계 ===' AS section;
SELECT
    category,
    ROUND(AVG(price)) AS avg_price,
    MIN(price) AS min_price,
    MAX(price) AS max_price,
    ROUND(STDDEV(price)) AS std_price
FROM fruit
GROUP BY category
ORDER BY avg_price DESC;

-- 9. name 카디널리티 (쿠폰 ~200종, 과일 ~30종)
SELECT '=== 9. name 카디널리티 ===' AS section;
SELECT 'coupon' AS table_name, COUNT(DISTINCT name) AS distinct_names FROM coupon
UNION ALL
SELECT 'fruit', COUNT(DISTINCT name) FROM fruit;

-- 10. ANALYZE TABLE
SELECT '=== 10. ANALYZE TABLE ===' AS section;
ANALYZE TABLE coupon;
ANALYZE TABLE fruit;
