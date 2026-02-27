package com.posty.lock.service;

import com.posty.lock.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuUpdateService {

    private final MenuRepository menuRepository;

    public MenuUpdateService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * 메뉴 이름 변경.
     * @DynamicUpdate 적용 시: UPDATE menu SET name = ? WHERE id = ?
     * @DynamicUpdate 미적용 시: UPDATE menu SET name = ?, price = ?, is_sold_out = ?, category = ? WHERE id = ?
     *
     * TODO: 메뉴 이름 변경 구현
     */
    @Transactional
    public void updateName(Long menuId, String newName) {
        // TODO: 메뉴 이름 변경 구현
        // 1. menuRepository.findById(menuId)
        // 2. menu.updateName(newName)
        // 3. @DynamicUpdate에 의해 변경된 컬럼만 UPDATE 쿼리에 포함
    }

    /**
     * 메뉴 품절 처리.
     * @DynamicUpdate 적용 시: UPDATE menu SET is_sold_out = ? WHERE id = ?
     *
     * TODO: 메뉴 품절 처리 구현
     */
    @Transactional
    public void markSoldOut(Long menuId) {
        // TODO: 메뉴 품절 처리 구현
        // 1. menuRepository.findById(menuId)
        // 2. menu.markSoldOut()
    }
}
