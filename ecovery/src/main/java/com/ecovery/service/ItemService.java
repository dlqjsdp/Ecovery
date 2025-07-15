package com.ecovery.service;

/*
 * 에코마켓 상품 Service
 * @author : sehui
 * @fileName : ItemService
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 기능 추가
 *  - 250714 | sehui | 전체 상품 조회 기능 추가
 *  - 250715 | sehui | 전체 상품의 수 조회 기능 추가
 *  - 250715 | sehui | 전체 상품, 상품의 수 조회에 단일 조건 검색 추가
 */

import com.ecovery.domain.ItemVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemListDto;

import java.util.List;

public interface ItemService {

    //전체 상품 조회
    public List<ItemListDto> getItemList(String itemNm, String category, Criteria cri);

    //상품 단 건 조회
    public ItemFormDto getItemDtl(Long itemId);

    //전체 상품의 수 조회
    public int getTotalCount(String itemNm, String category);
}
