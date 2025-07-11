package com.ecovery.mapper;

/*
 * 에코마켓 상품 Mapper
 * @author : sehui
 * @fileName : ItemMapper
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 기능 추가
 */

import com.ecovery.domain.ItemVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper {

    //상품 단 건 조회
    public ItemVO getItemDtl(Long itemId);
}
