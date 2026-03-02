#!/usr/bin/env bash
# ============================================
# module-index 학습용 테스트 데이터 적재 스크립트
#
# CSV 생성 → docker cp → LOAD DATA INFILE → 검증
# ============================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONTAINER="posty-sandbox"
DB_USER="root"
DB_PASS="root"
DB_NAME="posty"
MYSQL_CMD="docker exec -i ${CONTAINER} mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME}"

echo "============================================"
echo " module-index 테스트 데이터 적재"
echo "============================================"
echo

# Step 1: venv 준비 및 numpy 설치
VENV_DIR="${SCRIPT_DIR}/.venv"
echo "[1/6] Python venv 준비..."
if [ ! -d "${VENV_DIR}" ]; then
    echo "  venv 생성 중..."
    python3 -m venv "${VENV_DIR}"
fi
source "${VENV_DIR}/bin/activate"

if ! python3 -c "import numpy" 2>/dev/null; then
    echo "  numpy 설치 중..."
    pip install numpy
else
    echo "  numpy 이미 설치됨"
fi
echo

# Step 2: CSV 생성
echo "[2/6] CSV 생성 중..."
cd "${SCRIPT_DIR}"
python3 generate_test_data.py
deactivate
echo

# Step 3: Docker 컨테이너 확인
echo "[3/6] Docker 컨테이너 확인..."
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
    echo "  ERROR: ${CONTAINER} 컨테이너가 실행 중이 아닙니다."
    echo "  docker-compose up -d 로 먼저 실행하세요."
    exit 1
fi
echo "  ${CONTAINER} 실행 중 확인"
echo

# Step 4: CSV → 컨테이너로 복사
echo "[4/6] CSV 파일을 컨테이너로 복사..."
docker cp "${SCRIPT_DIR}/coupon.csv" "${CONTAINER}:/var/lib/mysql-files/coupon.csv"
docker cp "${SCRIPT_DIR}/fruit.csv" "${CONTAINER}:/var/lib/mysql-files/fruit.csv"
echo "  복사 완료"
echo

# Step 5: 테이블 생성 → TRUNCATE → LOAD DATA
echo "[5/6] 데이터 적재 중..."
${MYSQL_CMD} <<'SQL'
SET autocommit = 0;
SET foreign_key_checks = 0;
SET unique_checks = 0;

-- 테이블 생성
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

-- Coupon 적재
LOAD DATA INFILE '/var/lib/mysql-files/coupon.csv'
INTO TABLE coupon
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(name, discount_amount, quantity, issue_started_at, issue_ended_at, status, created_at, updated_at);

-- Fruit 적재
LOAD DATA INFILE '/var/lib/mysql-files/fruit.csv'
INTO TABLE fruit
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(name, price, category, created_at, updated_at);

COMMIT;

SET autocommit = 1;
SET foreign_key_checks = 1;
SET unique_checks = 1;
SQL
echo "  적재 완료"
echo

# Step 6: 검증
echo "[6/6] 분포 검증..."
${MYSQL_CMD} < "${SCRIPT_DIR}/verify-distributions.sql"
echo

# 정리: 컨테이너 내 CSV 삭제
echo "컨테이너 내 CSV 파일 정리..."
docker exec "${CONTAINER}" rm -f /var/lib/mysql-files/coupon.csv /var/lib/mysql-files/fruit.csv
echo "  정리 완료"
echo

echo "============================================"
echo " 모든 작업이 완료되었습니다!"
echo "============================================"
