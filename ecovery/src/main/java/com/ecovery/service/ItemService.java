package com.ecovery.service;

/*
 * 에코마켓 상품 Service
 * @author : sehui
 * @fileName : ItemService
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 기능 추가
 */

import com.ecovery.dto.ItemFormDto;

public interface ItemService {

    //상품 단 건 조회
    public ItemFormDto getItemDtl(Long itemId);
}
