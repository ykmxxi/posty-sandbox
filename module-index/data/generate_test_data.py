
"""
module-index 학습용 현실적 목 데이터 생성 스크립트

생성 대상:
  - coupon.csv (100만 건)
  - fruit.csv (50만 건)

의존성: numpy (pip install numpy)
실행:   python3 generate_test_data.py
"""

import csv
import os
from datetime import datetime, timedelta

import numpy as np

SEED = 42
NOW = datetime(2025, 3, 1, 12, 0, 0)
OUTPUT_DIR = os.path.dirname(os.path.abspath(__file__))

# ============================================
# Coupon 100만 건
# ============================================
COUPON_COUNT = 1_000_000

COUPON_NAMES = [
    "신규가입 할인", "첫 구매 감사", "웰컴 쿠폰", "가입축하 혜택", "신규회원 특별",
    "여름 특가", "겨울 세일", "봄맞이 할인", "가을 페스티벌", "시즌 오프",
    "VIP 전용 할인", "프리미엄 혜택", "골드회원 특별", "다이아 전용", "로열 보너스",
    "이벤트 쿠폰", "깜짝 할인", "타임세일", "한정 특가", "플래시 딜",
    "생일 축하", "기념일 혜택", "감사 쿠폰", "사은 행사", "추천인 보상",
    "장바구니 할인", "재구매 감사", "리뷰 작성 보상", "출석 체크 보상", "미션 달성",
    "앱 전용 할인", "모바일 특가", "라이브 방송 할인", "SNS 공유 보상", "알림 수신 혜택",
    "무료배송 쿠폰", "배송비 할인", "당일배송 혜택", "새벽배송 할인", "묶음배송 할인",
    "카드사 제휴", "통신사 할인", "제휴 포인트", "간편결제 혜택", "페이 적립",
    "브랜드 위크", "카테고리 할인", "기획전 쿠폰", "테마 세일", "콜라보 할인",
    "설날 특가", "추석 감사", "블랙프라이데이", "사이버먼데이", "광군제 할인",
    "어버이날 감사", "어린이날 혜택", "밸런타인 특별", "화이트데이 할인", "크리스마스 세일",
    "1+1 쿠폰", "2+1 혜택", "반값 할인", "초특가 쿠폰", "득템 찬스",
    "오늘만 할인", "주말 특가", "평일 혜택", "야간 할인", "새벽 특가",
    "첫 주문 할인", "단골 감사", "충성고객 보상", "등급업 축하", "누적구매 혜택",
    "포인트 전환", "적립금 쿠폰", "캐시백 혜택", "마일리지 보너스", "스탬프 완성",
    "친구 초대", "공유 보상", "그룹 할인", "단체 주문 혜택", "대량구매 할인",
    "시식 이벤트", "체험 쿠폰", "샘플 제공", "트라이얼 혜택", "무료 체험",
    "교환 쿠폰", "업그레이드 혜택", "사이즈 교환", "컬러 변경", "옵션 추가",
    "신상품 할인", "프리오더 혜택", "얼리버드 할인", "런칭 기념", "한정판 쿠폰",
    "정기배송 할인", "구독 혜택", "자동주문 할인", "멤버십 쿠폰", "연간 회원 보상",
    "오프라인 전용", "매장 방문 혜택", "픽업 할인", "현장 할인", "팝업스토어 쿠폰",
    "환경 포인트", "에코백 할인", "리사이클 보상", "친환경 혜택", "제로웨이스트",
    "건강식품 할인", "유기농 혜택", "다이어트 쿠폰", "비건 할인", "글루텐프리 혜택",
    "반려동물 할인", "펫 용품 혜택", "사료 할인", "미용 쿠폰", "건강검진 할인",
    "도서 할인", "전자책 혜택", "구독권 할인", "학용품 쿠폰", "교육 콘텐츠 할인",
    "가전 할인", "디지털 혜택", "IT 기기 쿠폰", "액세서리 할인", "소모품 할인",
    "가구 할인", "인테리어 혜택", "홈데코 쿠폰", "리빙 할인", "수납용품 혜택",
    "뷰티 할인", "스킨케어 혜택", "메이크업 쿠폰", "향수 할인", "헤어케어 혜택",
    "패션 할인", "시즌 의류 혜택", "슈즈 쿠폰", "액세서리 혜택", "언더웨어 할인",
    "여행 할인", "호텔 혜택", "항공권 쿠폰", "렌터카 할인", "액티비티 혜택",
    "식당 할인", "카페 혜택", "배달 쿠폰", "외식 할인", "맛집 혜택",
    "스포츠 할인", "피트니스 혜택", "골프 쿠폰", "캠핑 할인", "아웃도어 혜택",
    "키즈 할인", "유아용품 혜택", "장난감 쿠폰", "아기옷 할인", "분유 할인",
    "시니어 할인", "실버 혜택", "경로 할인", "효도 쿠폰", "건강기능식품 할인",
    "웨딩 할인", "신혼부부 혜택", "허니문 쿠폰", "예식장 할인", "스드메 할인",
    "졸업 축하", "입학 혜택", "시험 응원", "합격 기념", "자격증 할인",
    "개업 축하", "사업자 혜택", "대량주문 쿠폰", "납품 할인", "B2B 혜택",
    "중고거래 쿠폰", "리퍼브 할인", "전시품 혜택", "아울렛 할인", "땡처리 쿠폰",
]

COUPON_STATUSES = ["EXPIRED", "ACTIVE", "UPCOMING"]

# ============================================
# Fruit 50만 건
# ============================================
FRUIT_COUNT = 500_000

FRUIT_CATEGORIES = ["CITRUS", "TROPICAL", "BERRY", "STONE_FRUIT", "OTHER"]
FRUIT_CATEGORY_PROBS = [0.40, 0.25, 0.15, 0.12, 0.08]

FRUIT_NAMES_BY_CATEGORY = {
    "CITRUS": ["오렌지", "레몬", "자몽", "귤", "라임", "유자", "금귤"],
    "TROPICAL": ["망고", "파인애플", "바나나", "파파야", "코코넛", "구아바", "리치"],
    "BERRY": ["딸기", "블루베리", "라즈베리", "블랙베리", "크랜베리"],
    "STONE_FRUIT": ["복숭아", "자두", "체리", "살구", "매실"],
    "OTHER": ["사과", "배", "포도", "감", "키위", "무화과"],
}

FRUIT_PRICE_PARAMS = {
    "TROPICAL": (4500, 1500),
    "STONE_FRUIT": (3000, 1000),
    "CITRUS": (2500, 800),
    "OTHER": (2000, 900),
    "BERRY": (1800, 600),
}


def generate_coupons(rng: np.random.Generator) -> None:
    print(f"[1/2] Coupon {COUPON_COUNT:,}건 생성 중...")

    n = COUPON_COUNT

    # --- name: Zipf 분포 (~200종, 상위 10종이 ~40%) ---
    name_count = len(COUPON_NAMES)
    weights = np.array([1.0 / (i + 1) ** 0.8 for i in range(name_count)])
    weights /= weights.sum()
    name_indices = rng.choice(name_count, size=n, p=weights)

    # --- discount_amount: 1000/3000/5000에 70% 집중 ---
    is_anchor = rng.random(n) < 0.7
    anchor_values = rng.choice(
        [1000, 3000, 5000], size=n, p=[0.4, 0.35, 0.25]
    )
    long_tail_values = (rng.integers(1, 101, size=n) * 100).astype(np.int64)
    discount_amounts = np.where(is_anchor, anchor_values, long_tail_values)

    # --- issue_started_at: 지수 분포 (최근 편향) ---
    days_ago = rng.exponential(scale=200, size=n)
    days_ago = np.clip(days_ago, 0, 730).astype(int)
    issue_started = np.array(
        [NOW - timedelta(days=int(d)) for d in days_ago]
    )

    # --- status: 날짜 상관관계 ---
    statuses = np.empty(n, dtype=object)
    age_days = days_ago.astype(int)

    # 각 age 구간별 조건부 확률
    bins = [
        (age_days > 365, [0.85, 0.10, 0.05]),
        ((age_days > 180) & (age_days <= 365), [0.60, 0.30, 0.10]),
        ((age_days > 30) & (age_days <= 180), [0.30, 0.50, 0.20]),
        (age_days <= 30, [0.10, 0.40, 0.50]),
    ]
    for mask, probs in bins:
        count = mask.sum()
        if count > 0:
            statuses[mask] = rng.choice(COUPON_STATUSES, size=count, p=probs)

    # --- issue_ended_at: started + 7~60일 ---
    end_offsets = 7 + rng.integers(0, 54, size=n)
    issue_ended = np.array(
        [
            issue_started[i] + timedelta(days=int(end_offsets[i]))
            for i in range(n)
        ]
    )

    # --- quantity: 로그정규 분포 (중앙값 ~100) ---
    quantities = np.clip(
        rng.lognormal(mean=np.log(100), sigma=1.0, size=n).astype(int),
        1,
        10000,
    )

    # --- created_at / updated_at ---
    created_offsets = rng.integers(0, 31, size=n)
    created_at = np.array(
        [
            issue_started[i] - timedelta(days=int(created_offsets[i]))
            for i in range(n)
        ]
    )
    updated_offsets = rng.integers(0, 8, size=n)
    updated_at = np.array(
        [
            created_at[i] + timedelta(days=int(updated_offsets[i]))
            for i in range(n)
        ]
    )

    # --- CSV 출력 ---
    output_path = os.path.join(OUTPUT_DIR, "coupon.csv")
    fmt = "%Y-%m-%d %H:%M:%S.000000"
    with open(output_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        for i in range(n):
            writer.writerow(
                [
                    COUPON_NAMES[name_indices[i]],
                    int(discount_amounts[i]),
                    int(quantities[i]),
                    issue_started[i].strftime(fmt),
                    issue_ended[i].strftime(fmt),
                    statuses[i],
                    created_at[i].strftime(fmt),
                    updated_at[i].strftime(fmt),
                ]
            )
            if (i + 1) % 200_000 == 0:
                print(f"  coupon: {i + 1:>10,} / {n:,}")

    size_mb = os.path.getsize(output_path) / (1024 * 1024)
    print(f"  coupon.csv 완료 ({size_mb:.1f} MB)")


def generate_fruits(rng: np.random.Generator) -> None:
    print(f"[2/2] Fruit {FRUIT_COUNT:,}건 생성 중...")

    n = FRUIT_COUNT

    # --- category ---
    categories = rng.choice(
        FRUIT_CATEGORIES, size=n, p=FRUIT_CATEGORY_PROBS
    )

    # --- name: 카테고리별 과일 매핑 ---
    names = np.empty(n, dtype=object)
    for cat, fruit_list in FRUIT_NAMES_BY_CATEGORY.items():
        mask = categories == cat
        count = mask.sum()
        if count > 0:
            names[mask] = rng.choice(fruit_list, size=count)

    # --- price: 카테고리별 정규분포 ---
    prices = np.zeros(n, dtype=int)
    for cat, (mean, std) in FRUIT_PRICE_PARAMS.items():
        mask = categories == cat
        count = mask.sum()
        if count > 0:
            raw = rng.normal(loc=mean, scale=std, size=count)
            rounded = (np.round(raw / 100) * 100).astype(int)
            prices[mask] = np.clip(rounded, 500, 15000)

    # --- created_at: 지수 분포 (최근 편향) ---
    days_ago = np.clip(
        rng.exponential(scale=150, size=n), 0, 730
    ).astype(int)
    created_at = np.array(
        [NOW - timedelta(days=int(d)) for d in days_ago]
    )

    # --- updated_at ---
    updated_offsets = rng.integers(0, 4, size=n)
    updated_at = np.array(
        [
            created_at[i] + timedelta(days=int(updated_offsets[i]))
            for i in range(n)
        ]
    )

    # --- CSV 출력 ---
    output_path = os.path.join(OUTPUT_DIR, "fruit.csv")
    fmt = "%Y-%m-%d %H:%M:%S.000000"
    with open(output_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        for i in range(n):
            writer.writerow(
                [
                    names[i],
                    int(prices[i]),
                    categories[i],
                    created_at[i].strftime(fmt),
                    updated_at[i].strftime(fmt),
                ]
            )
            if (i + 1) % 100_000 == 0:
                print(f"  fruit: {i + 1:>10,} / {n:,}")

    size_mb = os.path.getsize(output_path) / (1024 * 1024)
    print(f"  fruit.csv 완료 ({size_mb:.1f} MB)")


def main() -> None:
    print("=" * 50)
    print("module-index 학습용 테스트 데이터 생성")
    print("=" * 50)

    rng = np.random.default_rng(SEED)

    generate_coupons(rng)
    generate_fruits(rng)

    print()
    print("모든 CSV 생성 완료!")


if __name__ == "__main__":
    main()
