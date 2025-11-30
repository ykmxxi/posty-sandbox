-- 1. Guest 데이터 (30명)
INSERT INTO `guest` (`nickname`) VALUES
('김철수'), ('이영희'), ('박민수'), ('정수연'), ('최동욱'),
('강지원'), ('윤서현'), ('임하늘'), ('한별'), ('오태양'),
('신바다'), ('조구름'), ('황산'), ('문달'), ('배하늘'),
('서은지'), ('노준호'), ('홍길동'), ('안중근'), ('유관순'),
('김유신'), ('이순신'), ('세종대왕'), ('신사임당'), ('허준'),
('장보고'), ('김구'), ('윤봉길'), ('안창호'), ('김좌진');

-- 2. Host 데이터 (15명)
INSERT INTO `host` (`name`, `picture_url`, `agreed_terms`) VALUES
('카페 사장님', 'https://example.com/host1.jpg', 1),
('갤러리 관장님', 'https://example.com/host2.jpg', 1),
('서점 주인', 'https://example.com/host3.jpg', 1),
('공방 대표', 'https://example.com/host4.jpg', 1),
('레스토랑 셰프', 'https://example.com/host5.jpg', 1),
('전시관 큐레이터', 'https://example.com/host6.jpg', 1),
('북카페 운영자', 'https://example.com/host7.jpg', 1),
('공유오피스 매니저', 'https://example.com/host8.jpg', 1),
('문화공간 기획자', 'https://example.com/host9.jpg', 1),
('팝업스토어 운영자', 'https://example.com/host10.jpg', 1),
('스튜디오 대표', 'https://example.com/host11.jpg', 1),
('베이커리 오너', 'https://example.com/host12.jpg', 1),
('플라워샵 대표', 'https://example.com/host13.jpg', 1),
('공예공방 운영자', 'https://example.com/host14.jpg', 1),
('복합문화공간 대표', 'https://example.com/host15.jpg', 1);

-- 3. HostKakao 데이터
INSERT INTO `host_kakao` (`host_id`, `user_id`) VALUES
(1, 'kakao_user_001'),
(2, 'kakao_user_002'),
(3, 'kakao_user_003'),
(4, 'kakao_user_004'),
(5, 'kakao_user_005'),
(6, 'kakao_user_006'),
(7, 'kakao_user_007'),
(8, 'kakao_user_008'),
(9, 'kakao_user_009'),
(10, 'kakao_user_010'),
(11, 'kakao_user_011'),
(12, 'kakao_user_012'),
(13, 'kakao_user_013'),
(14, 'kakao_user_014'),
(15, 'kakao_user_015');

-- 4. Space 데이터 (10개)
INSERT INTO `space` (`code`, `name`, `description`, `is_public`, `instagram_username`, `email`) VALUES
('CAFE001', '아늑한 북카페', '책과 커피가 있는 편안한 공간', 1, 'cozy_bookcafe', 'contact@cozybookcafe.com'),
('GALLERY001', '모던 아트 갤러리', '현대미술 작품 전시 공간', 1, 'modern_art_gallery', 'info@modernart.com'),
('BOOK001', '작은 서점', '독립서점으로 큐레이션된 책들', 1, 'small_bookstore', 'hello@smallbook.com'),
('CRAFT001', '도자기 공방', '도자기를 만들고 배우는 공간', 1, 'pottery_workshop', 'pottery@craft.com'),
('RESTO001', '이탈리안 레스토랑', '정통 이탈리안 요리 전문점', 1, 'italian_restaurant', 'contact@italian.com'),
('EXHIBIT001', '사진 전시관', '사진 작가들의 작품 전시', 1, 'photo_exhibition', 'photo@exhibit.com'),
('CAFE002', '루프탑 카페', '탁 트인 전망의 루프탑 카페', 1, 'rooftop_cafe', 'rooftop@cafe.com'),
('OFFICE001', '코워킹 스페이스', '1인 창업가를 위한 공유 오피스', 1, 'coworking_space', 'hello@coworking.com'),
('CULTURE001', '복합문화공간', '전시, 공연, 강연이 있는 문화공간', 1, 'culture_space', 'info@culture.com'),
('POPUP001', '팝업스토어', '매달 새로운 브랜드 팝업', 1, 'popup_store', 'popup@store.com');

-- 5. SpaceHostMap 데이터 (각 Space마다 Host 1-2명)
INSERT INTO `space_host_map` (`space_id`, `host_id`) VALUES
(1, 1), (1, 7),
(2, 2), (2, 6),
(3, 3),
(4, 4), (4, 14),
(5, 5),
(6, 6),
(7, 1),
(8, 8),
(9, 9), (9, 15),
(10, 10);

-- 6. GuestBookCard 데이터 (각 Space마다 20-30개, 총 250개)
-- Space 1 (CAFE001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(1, 1, '너무 아늑하고 좋아요! 책 읽기 딱 좋은 곳이에요', 1),
(1, 2, '커피가 정말 맛있어요. 자주 올게요!', 1),
(1, 3, '조용하고 편안한 분위기가 마음에 들어요', 0),
(1, 4, '인테리어가 너무 예뻐요', 1),
(1, 5, '친구들과 함께 오기 좋은 곳', 0),
(1, 6, '주말에 여유롭게 책 읽기 좋아요', 1),
(1, 7, '디저트도 맛있고 커피도 좋아요', 1),
(1, 8, '혼자 와서 책 읽기 딱 좋은 공간', 0),
(1, 9, '와이파이도 빠르고 좌석도 편해요', 1),
(1, 10, '책 선정이 너무 좋아요', 1),
(1, 11, '분위기 있고 감성적인 공간', 0),
(1, 12, '데이트 코스로 추천합니다', 1),
(1, 13, '조명이 따뜻해서 좋아요', 1),
(1, 14, '북카페 중에 최고예요', 0),
(1, 15, '책도 사고 커피도 마시고 일석이조', 1),
(1, 16, '주차도 편하고 접근성 좋아요', 0),
(1, 17, '직원분들이 친절해요', 1),
(1, 18, '책 추천 받아서 좋은 책 발견했어요', 1),
(1, 19, '가격도 합리적이고 만족스러워요', 0),
(1, 20, '단골 될 것 같아요', 1),
(1, 21, '창가 자리가 특히 좋아요', 1),
(1, 22, '음악도 좋고 분위기 최고', 0),
(1, 23, '친구 만나기 좋은 장소', 1),
(1, 24, '책장 구경하는 재미가 있어요', 0),
(1, 25, '또 방문하고 싶은 곳', 1);

-- Space 2 (GALLERY001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(2, 1, '작품이 너무 인상적이었어요', 1),
(2, 2, '현대미술의 깊이를 느낄 수 있었습니다', 1),
(2, 3, '전시 기획이 훌륭해요', 0),
(2, 4, '작가님과의 대화가 의미있었어요', 1),
(2, 5, '공간 활용이 좋아요', 0),
(2, 6, '조명이 작품을 더 돋보이게 해요', 1),
(2, 7, '미술 애호가에게 추천합니다', 1),
(2, 8, '감성 충전하기 좋은 곳', 0),
(2, 9, '전시 설명이 자세해서 좋았어요', 1),
(2, 10, '다음 전시도 기대됩니다', 1),
(2, 11, '작품 하나하나 의미가 깊어요', 0),
(2, 12, '사진 찍기 좋은 갤러리', 1),
(2, 13, '입장료가 아깝지 않아요', 1),
(2, 14, '큐레이터님 설명이 도움됐어요', 0),
(2, 15, '예술적 영감을 받고 가요', 1),
(2, 16, '정기적으로 방문하고 싶어요', 0),
(2, 17, '친구들에게 추천했어요', 1),
(2, 18, '갤러리 투어가 알차요', 1),
(2, 19, '작품 구매도 고려중입니다', 0),
(2, 20, '전시 주제가 흥미로워요', 1),
(2, 21, '현대미술 입문에 좋아요', 1),
(2, 22, '공간이 넓고 쾌적해요', 0),
(2, 23, '작품 배치가 세심해요', 1),
(2, 24, '미술관보다 더 좋은 것 같아요', 0),
(2, 25, '갤러리 카페도 운영하면 좋겠어요', 1);

-- Space 3 (BOOK001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(3, 6, '독립서점의 매력이 느껴져요', 1),
(3, 7, '책 큐레이션이 정말 좋아요', 1),
(3, 8, '주인장의 취향이 느껴지는 서점', 0),
(3, 9, '숨겨진 명작들을 발견했어요', 1),
(3, 10, '책방 투어하기 좋아요', 0),
(3, 11, '동네 책방의 온기가 있어요', 1),
(3, 12, '책과 사람을 잇는 공간', 1),
(3, 13, '작은 서점의 큰 매력', 0),
(3, 14, '책 추천이 정확해요', 1),
(3, 15, '독서 모임 하기 좋을 것 같아요', 1),
(3, 16, '서점지기님이 친절하세요', 0),
(3, 17, '희귀본도 많이 있어요', 1),
(3, 18, '헌책방 같은 분위기', 1),
(3, 19, '오래 머물고 싶은 서점', 0),
(3, 20, '동네에 이런 서점이 있어 행복해요', 1),
(3, 21, '작가 사인회도 자주 열려요', 0),
(3, 22, '북클럽 활동이 활발해요', 1),
(3, 23, '선물하기 좋은 책들이 많아요', 1),
(3, 24, '독립출판물도 구비되어 있어요', 0),
(3, 25, '책을 사랑하는 사람들의 아지트', 1);

-- Space 4 (CRAFT001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(4, 11, '도자기 만들기 체험 재밌었어요', 1),
(4, 12, '선생님이 정말 친절하세요', 1),
(4, 13, '힐링되는 시간이었습니다', 0),
(4, 14, '나만의 그릇을 만들었어요', 1),
(4, 15, '공방 분위기가 좋아요', 0),
(4, 16, '물레 돌리는 재미가 쏠쏠해요', 1),
(4, 17, '정기 수업도 들어보고 싶어요', 1),
(4, 18, '완성품 받는 날이 기대돼요', 0),
(4, 19, '도자기에 대해 많이 배웠어요', 1),
(4, 20, '데이트로 추천합니다', 1),
(4, 21, '손으로 뭔가 만드는 즐거움', 0),
(4, 22, '스트레스 해소에 최고', 1),
(4, 23, '작품들이 다 예뻐요', 1),
(4, 24, '공방 굿즈도 구매했어요', 0),
(4, 25, '다음엔 접시 만들어보려고요', 1);

-- Space 5 (RESTO001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(5, 16, '파스타가 정말 맛있어요', 1),
(5, 17, '이탈리아에서 먹는 맛', 1),
(5, 18, '셰프님의 정성이 느껴져요', 0),
(5, 19, '와인 페어링이 완벽해요', 1),
(5, 20, '분위기도 음식도 최고', 0),
(5, 21, '특별한 날 오기 좋아요', 1),
(5, 22, '피자 도우가 진짜 맛있어요', 1),
(5, 23, '티라미수 꼭 드세요', 0),
(5, 24, '재방문 의사 100%', 1),
(5, 25, '직원분들 서비스가 좋아요', 1),
(5, 26, '가격 대비 훌륭해요', 0),
(5, 27, '리조또 강추합니다', 1),
(5, 28, '예약 필수 맛집', 1),
(5, 29, '인테리어도 이탈리아 느낌', 0),
(5, 30, '정통 이탈리안의 진수', 1);

-- Space 6 (EXHIBIT001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(6, 1, '사진 한 장 한 장이 작품이에요', 1),
(6, 2, '작가의 시선이 느껴져요', 1),
(6, 3, '감성 사진 전시회', 0),
(6, 4, '인생 사진 건졌어요', 1),
(6, 5, '사진 배우고 싶어졌어요', 0),
(6, 6, '전시 콘셉트가 좋아요', 1),
(6, 7, '포토존이 많아요', 1),
(6, 8, 'SNS 감성 뿜뿜', 0),
(6, 9, '작가와의 대화 시간이 유익했어요', 1),
(6, 10, '사진전 처음인데 좋았어요', 1),
(6, 11, '흑백 사진의 매력', 0),
(6, 12, '도심 속 힐링 공간', 1),
(6, 13, '친구들과 오기 좋아요', 1),
(6, 14, '무료 입장인데 퀄리티 최고', 0),
(6, 15, '사진 인화해서 가져가고 싶어요', 1),
(6, 16, '다음 전시도 기대됩니다', 0),
(6, 17, '조용히 감상하기 좋아요', 1),
(6, 18, '작품 설명이 감동적', 1),
(6, 19, '사진의 깊이를 느꼈어요', 0),
(6, 20, '전시 구성이 탄탄해요', 1),
(6, 21, '감성 충전 완료', 1),
(6, 22, '데이트 코스로 딱', 0),
(6, 23, '작가님 팬 됐어요', 1),
(6, 24, '사진집도 구매했어요', 0),
(6, 25, '또 오고 싶은 전시관', 1);

-- Space 7 (CAFE002) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(7, 11, '뷰가 정말 끝내줘요', 1),
(7, 12, '석양 보기 좋은 카페', 1),
(7, 13, '루프탑 분위기 최고', 0),
(7, 14, '야경이 아름다워요', 1),
(7, 15, '시원한 바람 맞으며 커피 한잔', 0),
(7, 16, '날씨 좋을 때 꼭 오세요', 1),
(7, 17, '사진 맛집입니다', 1),
(7, 18, '커피도 맛있고 뷰도 좋고', 0),
(7, 19, '힙한 분위기', 1),
(7, 20, '젊은 사람들이 많아요', 1),
(7, 21, '음악도 좋아요', 0),
(7, 22, '웨이팅 있어도 기다릴 만해요', 1),
(7, 23, '야외 테이블 강추', 1),
(7, 24, '도심 속 휴식처', 0),
(7, 25, '인스타 감성 카페', 1);

-- Space 8 (OFFICE001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(8, 16, '집중하기 좋은 환경', 1),
(8, 17, '시설이 깔끔해요', 1),
(8, 18, '네트워킹 기회가 많아요', 0),
(8, 19, '1인 기업하기 딱 좋아요', 1),
(8, 20, '회의실 시설 훌륭해요', 0),
(8, 21, '커피 무제한이 좋아요', 1),
(8, 22, '조용하고 쾌적해요', 1),
(8, 23, '스타트업 분위기', 0),
(8, 24, '멘토링 프로그램이 유익해요', 1),
(8, 25, '창업 초기에 추천합니다', 1),
(8, 26, '입주사들이 좋은 분들이에요', 0),
(8, 27, '이벤트도 자주 열려요', 1),
(8, 28, '가성비 최고', 1),
(8, 29, '업무 효율이 올라가요', 0),
(8, 30, '장기 계약 고민중', 1);

-- Space 9 (CULTURE001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(9, 21, '복합문화공간의 모범', 1),
(9, 22, '전시도 보고 공연도 보고', 1),
(9, 23, '프로그램이 다양해요', 0),
(9, 24, '문화생활하기 좋아요', 1),
(9, 25, '강연 정말 유익했어요', 0),
(9, 26, '예술가들의 아지트', 1),
(9, 27, '창작 활동 지원이 좋아요', 1),
(9, 28, '커뮤니티 형성이 잘 돼있어요', 0),
(9, 29, '문화 감수성이 높아져요', 1),
(9, 30, '매달 오고 싶어요', 1);

-- Space 10 (POPUP001) - 25개
INSERT INTO `guest_book_card` (`space_id`, `guest_id`, `message`, `is_read`) VALUES
(10, 26, '이번 팝업 브랜드 너무 좋아요', 1),
(10, 27, '매번 새로운 재미', 1),
(10, 28, '한정판 득템했어요', 0),
(10, 29, '굿즈 퀄리티 좋아요', 1),
(10, 30, '팝업 소식 자주 올려주세요', 0),
(10, 1, '오픈런 성공했어요', 1),
(10, 2, '트렌디한 브랜드들', 1),
(10, 3, '포토존이 예뻐요', 0),
(10, 4, '쇼핑하기 좋아요', 1),
(10, 5, '다음 팝업도 기대돼요', 1),
(10, 6, '희귀템 발견', 0),
(10, 7, '직원분들 친절해요', 1),
(10, 8, '브랜드 경험이 좋았어요', 1),
(10, 9, 'SNS 이벤트도 많아요', 0),
(10, 10, '힙한 팝업스토어', 1);

-- 7. GuestBookCardPhoto 데이터 (각 카드마다 1-3개)
-- Space 1의 카드들 사진
INSERT INTO `guest_book_card_photo` (`guest_book_card_id`, `original_name`, `path`, `capacity`) VALUES
(1, 'cafe_interior_1.jpg', '/photos/guestbook/1/1.jpg', 1024000),
(1, 'cafe_coffee_1.jpg', '/photos/guestbook/1/2.jpg', 856000),
(2, 'cafe_latte_1.jpg', '/photos/guestbook/2/1.jpg', 920000),
(3, 'cafe_reading_1.jpg', '/photos/guestbook/3/1.jpg', 1156000),
(3, 'cafe_reading_2.jpg', '/photos/guestbook/3/2.jpg', 998000),
(4, 'cafe_deco_1.jpg', '/photos/guestbook/4/1.jpg', 1234000),
(5, 'cafe_friends_1.jpg', '/photos/guestbook/5/1.jpg', 1456000),
(5, 'cafe_friends_2.jpg', '/photos/guestbook/5/2.jpg', 1123000),
(5, 'cafe_friends_3.jpg', '/photos/guestbook/5/3.jpg', 1034000),
(6, 'cafe_book_1.jpg', '/photos/guestbook/6/1.jpg', 876000),
(7, 'cafe_dessert_1.jpg', '/photos/guestbook/7/1.jpg', 1167000),
(7, 'cafe_dessert_2.jpg', '/photos/guestbook/7/2.jpg', 945000),
(8, 'cafe_solo_1.jpg', '/photos/guestbook/8/1.jpg', 789000),
(9, 'cafe_workspace_1.jpg', '/photos/guestbook/9/1.jpg', 1345000),
(10, 'cafe_bookshelf_1.jpg', '/photos/guestbook/10/1.jpg', 1089000);

-- Space 2의 카드들 사진
INSERT INTO `guest_book_card_photo` (`guest_book_card_id`, `original_name`, `path`, `capacity`) VALUES
(26, 'gallery_art_1.jpg', '/photos/guestbook/26/1.jpg', 2156000),
(26, 'gallery_art_2.jpg', '/photos/guestbook/26/2.jpg', 1987000),
(27, 'gallery_sculpture_1.jpg', '/photos/guestbook/27/1.jpg', 2234000),
(28, 'gallery_painting_1.jpg', '/photos/guestbook/28/1.jpg', 1876000),
(28, 'gallery_painting_2.jpg', '/photos/guestbook/28/2.jpg', 2001000),
(29, 'gallery_installation_1.jpg', '/photos/guestbook/29/1.jpg', 2345000),
(30, 'gallery_exhibition_1.jpg', '/photos/guestbook/30/1.jpg', 1945000);

-- Space 3의 카드들 사진
INSERT INTO `guest_book_card_photo` (`guest_book_card_id`, `original_name`, `path`, `capacity`) VALUES
(51, 'bookstore_shelf_1.jpg', '/photos/guestbook/51/1.jpg', 1234000),
(52, 'bookstore_books_1.jpg', '/photos/guestbook/52/1.jpg', 1456000),
(52, 'bookstore_books_2.jpg', '/photos/guestbook/52/2.jpg', 1123000),
(53, 'bookstore_corner_1.jpg', '/photos/guestbook/53/1.jpg', 987000),
(54, 'bookstore_display_1.jpg', '/photos/guestbook/54/1.jpg', 1567000);

-- 8. Product 데이터 (각 Space마다 10-15개)
-- Space 1 (CAFE001) 제품들
INSERT INTO `product` (`space_id`, `title`, `category`, `author_name`, `description`, `video_url`, `is_video_after_photo`) VALUES
(1, '아메리카노', '음료', '카페 사장님', '직접 로스팅한 원두로 만든 깊은 맛의 아메리카노입니다. 에티오피아 예가체프 원두를 사용하여 풍부한 과일향과 깔끔한 뒷맛이 특징입니다.', '', 0),
(1, '카페라떼', '음료', '카페 사장님', '부드러운 우유 거품과 진한 에스프레소의 조화. 라떼 아트로 시각적인 즐거움까지 더했습니다.', '', 0),
(1, '카페모카', '음료', '카페 사장님', '진한 초콜릿과 에스프레소의 달콤쌉싸름한 조화. 휘핑크림 토핑으로 더욱 풍성하게 즐기실 수 있습니다.', '', 0),
(1, '바닐라라떼', '음료', '카페 사장님', '천연 바닐라 시럽과 부드러운 우유의 달콤한 조화. 카페인이 부담스러운 분들께 추천드립니다.', '', 0),
(1, '콜드브루', '음료', '카페 사장님', '24시간 저온 추출한 콜드브루. 부드러우면서도 깊은 맛이 특징입니다.', '', 0),
(1, '티라미수', '디저트', '파티시에', '마스카포네 치즈와 에스프레소가 어우러진 정통 이탈리안 티라미수. 커피와 함께 드시면 더욱 좋습니다.', '', 0),
(1, '치즈케이크', '디저트', '파티시에', '뉴욕 스타일의 진한 치즈케이크. 촘촘하고 부드러운 식감이 일품입니다.', '', 0),
(1, '마들렌', '디저트', '파티시에', '버터의 풍미가 가득한 프랑스식 마들렌. 홍차나 커피와 잘 어울립니다.', '', 0),
(1, '스콘', '디저트', '파티시에', '겉은 바삭하고 속은 촉촉한 영국식 스콘. 클로티드 크림과 잼을 곁들여 드세요.', '', 0),
(1, '크루아상', '디저트', '파티시에', '겹겹이 쌓인 버터 레이어가 만들어내는 바삭한 식감. 매일 아침 신선하게 구워냅니다.', '', 0),
(1, '카페 텀블러', '굿즈', '카페 사장님', '카페 로고가 새겨진 에코 텀블러. 텀블러 사용시 음료 500원 할인 혜택이 있습니다.', '', 0),
(1, '드립백 커피', '원두', '카페 사장님', '집에서도 카페의 맛을 즐길 수 있는 드립백 커피. 10개입 세트입니다.', '', 0);

-- Space 2 (GALLERY001) 작품들
INSERT INTO `product` (`space_id`, `title`, `category`, `author_name`, `description`, `video_url`, `is_video_after_photo`) VALUES
(2, '도시의 밤', '회화', '김현대', '도시의 야경을 추상적으로 표현한 작품. 아크릴 물감을 사용하여 빛과 어둠의 대비를 극대화했습니다. 100x150cm 캔버스에 그려진 대형 작품으로, 현대적인 공간에 잘 어울립니다.', '', 0),
(2, '무제 #17', '조각', '박조각', '스테인리스 스틸로 제작된 추상 조각. 빛의 각도에 따라 다양한 모습을 보여주는 것이 특징입니다. 높이 180cm의 대형 조각 작품입니다.', '', 0),
(2, '기억의 조각들', '설치미술', '이설치', '버려진 일상용품들을 재조합하여 만든 설치 작품. 현대 소비사회에 대한 비판적 시각을 담았습니다.', '', 0),
(2, '숲', '사진', '정사진', '한국의 숲을 흑백으로 담아낸 사진 시리즈. 총 12점으로 구성되어 있으며, 각 60x90cm 크기입니다.', '', 0),
(2, '경계', '회화', '최유화', '동양화 기법과 서양화 기법을 결합한 실험적 작품. 먹과 아크릴을 함께 사용했습니다.', '', 0),
(2, '시간의 흐름', '미디어아트', '강미디어', '디지털 기술을 활용한 인터랙티브 미디어 아트. 관람객의 움직임에 반응하여 변화하는 작품입니다.', 'https://example.com/videos/time_flow.mp4', 1),
(2, '붉은 여인', '회화', '신여인', '강렬한 붉은색으로 여성의 내면을 표현한 작품. 80x100cm 캔버스에 유화로 그려졌습니다.', '', 0),
(2, '도시 풍경 시리즈', '판화', '윤판화', '도시의 다양한 모습을 담은 실크스크린 판화 시리즈. 각 작품은 50부 한정 에디션입니다.', '', 0);

-- Space 3 (BOOK001) 도서들
INSERT INTO `product` (`space_id`, `title`, `category`, `author_name`, `description`, `video_url`, `is_video_after_photo`) VALUES
(3, '달러구트 꿈 백화점', '소설', '이미예', '잠들어야만 입장할 수 있는 신비한 꿈 백화점 이야기. 따뜻하고 위로가 되는 판타지 소설입니다.', '', 0),
(3, '미드나잇 라이브러리', '소설', '매트 헤이그', '삶과 죽음 사이, 무한한 가능성의 도서관 이야기. 인생의 의미를 되돌아보게 하는 감동적인 소설입니다.', '', 0),
(3, '아몬드', '소설', '손원평', '감정을 느끼지 못하는 소년의 성장 이야기. 청소년 문학의 수작입니다.', '', 0),
(3, '고요할수록 밝아지는 것들', '에세이', '혜민 스님', '삶의 지혜와 위로를 담은 에세이. 바쁜 일상 속에서 잠시 멈추고 싶을 때 읽기 좋습니다.', '', 0),
(3, '작별인사', '소설', '김영하', '우주선을 타고 떠나는 인류의 마지막 세대 이야기. SF와 인문학이 만난 독특한 소설입니다.', '', 0),
(3, '하마터면 열심히 살 뻔했다', '에세이', '하완', '열심히 사는 것보다 중요한 가치에 대한 성찰. 워라밸에 대해 고민하는 이들에게 권합니다.', '', 0),
(3, '공간이 만든 공간', '건축', '유현준', '공간이 인간에게 미치는 영향에 대한 인문 건축 에세이. 우리가 사는 공간을 새롭게 바라보게 합니다.', '', 0),
(3, '우리가 빛의 속도로 갈 수 없다면', '소설', '김초엽', '한국 SF의 새로운 가능성을 보여준 작품. 7편의 단편이 수록된 소설집입니다.', '', 0),
(3, '저는 농담으로 과학을 말합니다', '과학', '이정모', '재미있게 읽히는 과학 교양서. 어려운 과학을 유머러스하게 풀어냈습니다.', '', 0),
(3, '죽고 싶지만 떡볶이는 먹고 싶어', '에세이', '백세희', '우울증을 겪는 저자의 심리 상담 일기. 삶의 작은 행복을 발견하는 이야기입니다.', '', 0);

-- Space 4 (CRAFT001) 도자기 작품들
INSERT INTO `product` (`space_id`, `title`, `category`, `author_name`, `description`, `video_url`, `is_video_after_photo`) VALUES
(4, '백자 찻잔 세트', '도자기', '공방 대표', '전통 백자 기법으로 제작한 찻잔 세트. 5개 세트로 구성되어 있으며, 은은한 백색이 아름답습니다.', '', 0),
(4, '청자 꽃병', '도자기', '공방 대표', '고려청자를 현대적으로 재해석한 꽃병. 비취색 유약이 특징입니다.', '', 0),
(4, '분청 접시', '도자기', '공방 대표', '분청사기 기법으로 만든 접시. 투박하면서도 정감있는 느낌이 매력적입니다.', '', 0),
(4, '손잡이 머그컵', '도자기', '공방 대표', '일상에서 사용하기 좋은 머그컵. 직접 손으로 빚어 하나하나 다른 느낌이 있습니다.', '', 0),
(4, '밥그릇 세트', '도자기', '공방 대표', '매일 사용하는 밥그릇. 손에 잘 맞고 무게감이 적당합니다.', '', 0),
(4, '술잔 세트', '도자기', '공방 대표', '전통주를 즐기기 좋은 술잔 세트. 전통과 현대가 조화를 이룬 디자인입니다.', '', 0),
(4, '향로', '도자기', '공방 대표', '차분한 분위기를 만들어주는 향로. 인센스 스틱이나 콘 향 모두 사용 가능합니다.', '', 0),
(4, '티팟', '도자기', '공방 대표', '차를 우리기 좋은 티팟. 주둥이가 길어 따르기 편리합니다.', '', 0);

-- Space 5 (RESTO001) 메뉴들
INSERT INTO `product` (`space_id`, `title`, `category`, `author_name`, `description`, `video_url`, `is_video_after_photo`) VALUES
(5, '트러플 크림 파스타', '파스타', '레스토랑 셰프', '이탈리아산 트러플과 크림의 조화. 페투치네 면에 신선한 트러플을 갈아 올렸습니다. 와인 한 잔과 함께하면 완벽한 조합입니다.', '', 0),
(5, '까르보나라', '파스타', '레스토랑 셰프', '정통 로마식 까르보나라. 달걀 노른자와 페코리노 치즈, 관찰레로 만든 진짜 까르보나라를 경험하세요.', '', 0),
(5, '알리오 올리오', '파스타', '레스토랑 셰프', '마늘과 올리브오일의 심플한 맛. 심플하지만 깊은 맛을 내기 위해 정성을 다했습니다.', '', 0),
(5, '마르게리타 피자', '피자', '레스토랑 셰프', '토마토, 모짜렐라, 바질의 클래식한 조합. 나폴리식 화덕에서 구워 도우가 특히 맛있습니다.', 'https://example.com/videos/pizza_making.mp4', 1),
(5, '해산물 피자', '피자', '레스토랑 셰프', '신선한 해산물이 가득 올라간 피자. 새우, 오징어, 홍합 등이 풍성하게 들어갑니다.', '', 0),
(5, '리조또 알 네로', '리조또', '레스토랑 셰프', '오징어 먹물로 만든 검은 리조또. 해산물 육수의 깊은 맛이 일품입니다.', '', 0),
(5, '버섯 리조또', '리조또', '레스토랑 셰프', '포르치니 버섯의 향긋함이 가득한 리조또. 파르미지아노 치즈를 듬뿍 갈아드립니다.', '', 0),
(5, '티라미수', '디저트', '레스토랑 셰프', '마스카포네 치즈와 에스프레소의 완벽한 조화. 달콤쌉싸름한 정통 이탈리안 디저트입니다.', '', 0),
(5, '판나코타', '디저트', '레스토랑 셰프', '부드럽고 달콤한 이탈리안 푸딩. 베리 소스와 함께 제공됩니다.', '', 0),
(5, '젤라또', '디저트', '레스토랑 셰프', '이탈리아 정통 아이스크림. 바닐라, 초콜릿, 피스타치오 등 다양한 맛을 준비했습니다.', '', 0);

-- Space 6-10은 간략하게 추가
INSERT INTO `product` (`space_id`, `title`, `category`, `author_name`, `description`, `video_url`, `is_video_after_photo`) VALUES
-- Space 6 (EXHIBIT001)
(6, '흑백 도시 시리즈', '사진', '사진작가 A', '도시의 밤을 흑백으로 담아낸 사진 시리즈입니다.', '', 0),
(6, '인물 사진 컬렉션', '사진', '사진작가 B', '감성적인 인물 사진 작품들', '', 0),
-- Space 7 (CAFE002)
(7, '루프탑 시그니처 커피', '음료', '루프탑 바리스타', '루프탑에서만 맛볼 수 있는 특별한 블렌딩 커피', '', 0),
(7, '석양 칵테일', '음료', '루프탑 바리스타', '석양을 닮은 색감의 시그니처 칵테일', '', 0),
-- Space 8 (OFFICE001)
(8, '1인실 멤버십', '공간이용권', '코워킹 매니저', '1인 전용 독립 오피스 월 이용권', '', 0),
(8, '공용 좌석 멤버십', '공간이용권', '코워킹 매니저', '자유좌석 월 무제한 이용권', '', 0),
-- Space 9 (CULTURE001)
(9, '전시 관람권', '티켓', '문화공간 기획자', '이달의 전시 관람권', '', 0),
(9, '공연 티켓', '티켓', '문화공간 기획자', '주말 공연 예매 티켓', '', 0),
-- Space 10 (POPUP001)
(10, '한정판 굿즈 A', '굿즈', '팝업브랜드', '이번 팝업 한정 굿즈', '', 0),
(10, '콜라보 제품', '굿즈', '팝업브랜드', '브랜드 콜라보레이션 제품', '', 0);

-- 9. ProductPhoto 데이터 (각 상품마다 2-4개)
-- Space 1 제품들의 사진
INSERT INTO `product_photo` (`product_id`, `sort_order`, `original_name`, `path`, `capacity`) VALUES
(1, 1, 'americano_1.jpg', '/photos/product/1/1.jpg', 456000),
(1, 2, 'americano_2.jpg', '/photos/product/1/2.jpg', 512000),
(2, 1, 'latte_1.jpg', '/photos/product/2/1.jpg', 489000),
(2, 2, 'latte_art.jpg', '/photos/product/2/2.jpg', 567000),
(2, 3, 'latte_3.jpg', '/photos/product/2/3.jpg', 523000),
(3, 1, 'mocha_1.jpg', '/photos/product/3/1.jpg', 601000),
(3, 2, 'mocha_2.jpg', '/photos/product/3/2.jpg', 578000),
(4, 1, 'vanilla_latte_1.jpg', '/photos/product/4/1.jpg', 534000),
(4, 2, 'vanilla_latte_2.jpg', '/photos/product/4/2.jpg', 487000),
(5, 1, 'coldbrew_1.jpg', '/photos/product/5/1.jpg', 612000),
(5, 2, 'coldbrew_2.jpg', '/photos/product/5/2.jpg', 589000),
(6, 1, 'tiramisu_1.jpg', '/photos/product/6/1.jpg', 734000),
(6, 2, 'tiramisu_2.jpg', '/photos/product/6/2.jpg', 698000),
(6, 3, 'tiramisu_slice.jpg', '/photos/product/6/3.jpg', 645000),
(7, 1, 'cheesecake_1.jpg', '/photos/product/7/1.jpg', 712000),
(7, 2, 'cheesecake_2.jpg', '/photos/product/7/2.jpg', 689000),
(8, 1, 'madeleine_1.jpg', '/photos/product/8/1.jpg', 456000),
(8, 2, 'madeleine_2.jpg', '/photos/product/8/2.jpg', 478000),
(9, 1, 'scone_1.jpg', '/photos/product/9/1.jpg', 523000),
(9, 2, 'scone_2.jpg', '/photos/product/9/2.jpg', 501000),
(9, 3, 'scone_cream.jpg', '/photos/product/9/3.jpg', 489000),
(10, 1, 'croissant_1.jpg', '/photos/product/10/1.jpg', 567000),
(10, 2, 'croissant_2.jpg', '/photos/product/10/2.jpg', 543000),
(11, 1, 'tumbler_1.jpg', '/photos/product/11/1.jpg', 412000),
(11, 2, 'tumbler_2.jpg', '/photos/product/11/2.jpg', 398000),
(12, 1, 'dripbag_1.jpg', '/photos/product/12/1.jpg', 445000),
(12, 2, 'dripbag_package.jpg', '/photos/product/12/2.jpg', 467000);

-- Space 2 제품들의 사진
INSERT INTO `product_photo` (`product_id`, `sort_order`, `original_name`, `path`, `capacity`) VALUES
(13, 1, 'city_night_1.jpg', '/photos/product/13/1.jpg', 2345000),
(13, 2, 'city_night_2.jpg', '/photos/product/13/2.jpg', 2287000),
(13, 3, 'city_night_detail.jpg', '/photos/product/13/3.jpg', 2156000),
(14, 1, 'sculpture_1.jpg', '/photos/product/14/1.jpg', 1987000),
(14, 2, 'sculpture_2.jpg', '/photos/product/14/2.jpg', 2012000),
(14, 3, 'sculpture_detail.jpg', '/photos/product/14/3.jpg', 1865000),
(15, 1, 'installation_1.jpg', '/photos/product/15/1.jpg', 2456000),
(15, 2, 'installation_2.jpg', '/photos/product/15/2.jpg', 2389000),
(16, 1, 'forest_1.jpg', '/photos/product/16/1.jpg', 1756000),
(16, 2, 'forest_2.jpg', '/photos/product/16/2.jpg', 1823000),
(16, 3, 'forest_3.jpg', '/photos/product/16/3.jpg', 1789000);

-- Space 3 제품들의 사진 (도서)
INSERT INTO `product_photo` (`product_id`, `sort_order`, `original_name`, `path`, `capacity`) VALUES
(21, 1, 'book1_cover.jpg', '/photos/product/21/1.jpg', 567000),
(21, 2, 'book1_back.jpg', '/photos/product/21/2.jpg', 523000),
(22, 1, 'book2_cover.jpg', '/photos/product/22/1.jpg', 589000),
(22, 2, 'book2_back.jpg', '/photos/product/22/2.jpg', 545000),
(23, 1, 'book3_cover.jpg', '/photos/product/23/1.jpg', 512000),
(24, 1, 'book4_cover.jpg', '/photos/product/24/1.jpg', 534000),
(25, 1, 'book5_cover.jpg', '/photos/product/25/1.jpg', 578000);

-- 10. SpacePhoto 데이터 (각 Space마다 1개 - OneToOne 관계)
INSERT INTO `space_photo` (`space_id`, `original_name`, `path`, `capacity`) VALUES
(1, 'cafe_exterior.jpg', '/photos/space/1/exterior.jpg', 1567000),
(2, 'gallery_entrance.jpg', '/photos/space/2/entrance.jpg', 1789000),
(3, 'bookstore_front.jpg', '/photos/space/3/front.jpg', 1345000),
(4, 'workshop_studio.jpg', '/photos/space/4/studio.jpg', 1567000),
(5, 'restaurant_dining.jpg', '/photos/space/5/dining.jpg', 1678000),
(6, 'exhibit_entrance.jpg', '/photos/space/6/entrance.jpg', 1734000),
(7, 'rooftop_view.jpg', '/photos/space/7/view.jpg', 2034000),
(8, 'coworking_space.jpg', '/photos/space/8/space.jpg', 1534000),
(9, 'culture_hall.jpg', '/photos/space/9/hall.jpg', 1823000),
(10, 'popup_storefront.jpg', '/photos/space/10/storefront.jpg', 1645000);
