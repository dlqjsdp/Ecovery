package com.ecovery.service;

import com.ecovery.domain.ItemImgVO;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemImgDto;
import com.ecovery.mapper.ItemImgMapper;
import com.ecovery.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 에코마켓 상품 ServiceImpl
 * @author : sehui
 * @fileName : ItemServiceImpl
 * @since : 250709
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 기능 추가
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService     {

    private final ItemMapper itemMapper;
    private final ItemImgMapper itemImgMapper;

    //상품 수정 페이지 (상품 단 건 조회)
    @Override
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        log.info("getItem >>>");

        //ItemImgVO 조회
        List<ItemImgVO> itemImgList = itemImgMapper.getItemList(itemId);

        //ItemImgVo -> ItemImgDto로 변환
        List<ItemImgDto> itemImgDtoList = itemImgList.stream()
                .map(vo -> new ItemImgDto(vo.getItemImgId(), vo.getImgName(), vo.getOriImgName(), vo.getImgUrl(), vo.getRepImgYn()))
                .collect(Collectors.toList());

        //ItemVO 조회
        ItemVO item = itemMapper.getItemDtl(itemId);

        //ItemVO -> ItemFormDto로 변환
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }
}
