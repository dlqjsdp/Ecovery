package com.ecovery.service;

/*
 * 에코마켓 상품 이미지 Service
 * @author : sehui
 * @fileName : ItemService
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 이미지 전체 조회 기능 추가
 */

import com.ecovery.domain.ItemImgVO;

import java.util.List;

public interface ItemImgService {

    //상품 이미지 전체 조회
    public List<ItemImgVO> getItemList();
}
