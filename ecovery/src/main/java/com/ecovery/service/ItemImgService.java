package com.ecovery.service;

/*
 * 에코마켓 상품 이미지 Service
 * @author : sehui
 * @fileName : ItemService
 * @since : 250710
 * @history
 *  - 250716 | sehui | 상품 이미지 등록 기능 추가
 *  - 250716 | sehui | 상품 이미지 수정 기능 추가
 */

import com.ecovery.domain.ItemImgVO;
import com.ecovery.dto.ItemImgDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemImgService {

    //상품 이미지 등록
    public void saveItemImg(ItemImgVO itemImgVO, MultipartFile itemImgFile) throws Exception;

    //상품 이미지 수정
    public void updateItemImg(ItemImgVO itemImgVO, MultipartFile itemImgFile) throws Exception;
}
