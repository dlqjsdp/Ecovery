package com.ecovery.mapper;

/*
 * 에코마켓 상품 Mapper
 * @author : sehui
 * @fileName : ItemMapper
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 기능 추가
 *  - 250714 | sehui | 전체 상품 조회 기능 추가
 *  - 250715 | sehui | 전체 상품의 수 조회 기능 추가
 *  - 250715 | sehui | 전체 상품, 상품의 수 조회에 단일 조건 검색 추가
 */

import com.ecovery.domain.ItemVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemListDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ItemMapper {

    //전체 상품 조회
    public List<ItemListDto> getListWithPage(String itemNm, String category, Criteria cri);

    //상품 단 건 조회
    public ItemVO getItemDtl(Long itemId);

    //상품 등록

    //상품 수정

    //상품 삭제

    //전체 상품의 수 조회 (페이지 버튼)
    public int getTotalCount(String itemNm, String category);

}
