package com.ecovery.service;


import com.ecovery.domain.ItemImgVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * 에코마켓 상품 이미지 ServiceImpl
 * @author : sehui
 * @fileName : ItemImgServiceImpl
 * @since : 250709
 * @history
 *  - 250710 | sehui | 상품 이미지 전체 조회 기능 추가
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemImgServiceImpl implements ItemImgService {

    //전체 이미지 조회
    @Override
    public List<ItemImgVO> getItemList() {
        return List.of();
    }
}
