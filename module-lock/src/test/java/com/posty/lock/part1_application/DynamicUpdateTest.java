package com.posty.lock.part1_application;

import com.posty.lock.BaseIntegrationTest;
import com.posty.lock.repository.MenuRepository;
import com.posty.lock.service.MenuUpdateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Part1: @DynamicUpdate - 변경된 컬럼만 UPDATE 쿼리에 포함")
class DynamicUpdateTest extends BaseIntegrationTest {

    @Autowired
    private MenuUpdateService menuUpdateService;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    @DisplayName("@DynamicUpdate 적용 시 변경된 컬럼만 UPDATE 쿼리에 포함된다")
    void dynamicUpdate_onlyChangedColumns_inUpdateQuery() {
        // TODO: 구현
        // 1. 메뉴 생성
        // 2. 메뉴 이름만 변경
        // 3. p6spy 로그에서 UPDATE menu SET name = ? WHERE id = ? 확인
        //    (price, is_sold_out, category가 포함되지 않음)
    }

    @Test
    @DisplayName("매니저가 메뉴명 수정 + 주방에서 품절 처리가 동시에 발생해도 서로 다른 컬럼을 수정하므로 충돌하지 않는다")
    void dynamicUpdate_differentColumns_noConflict() {
        // TODO: 구현
        // 1. 메뉴 생성
        // 2. 스레드 A: 메뉴명 변경 (UPDATE SET name = ?)
        // 3. 스레드 B: 품절 처리 (UPDATE SET is_sold_out = ?)
        // 4. 두 변경 모두 반영되었는지 확인
    }

    @Test
    @DisplayName("@DynamicUpdate가 있어도 같은 컬럼을 동시에 수정하면 Lost Update가 발생한다")
    void dynamicUpdate_sameColumn_lostUpdate() {
        // TODO: 구현
        // 1. 메뉴 생성
        // 2. 스레드 A: 메뉴명을 "치킨"으로 변경
        // 3. 스레드 B: 메뉴명을 "피자"로 변경
        // 4. 한쪽 변경이 덮어씌워짐 → @DynamicUpdate는 만능이 아니다
    }
}
