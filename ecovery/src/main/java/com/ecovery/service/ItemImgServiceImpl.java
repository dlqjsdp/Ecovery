package com.ecovery.service;


import com.ecovery.domain.ItemImgVO;
import com.ecovery.dto.ItemImgDto;
import com.ecovery.mapper.ItemImgMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 에코마켓 상품 이미지 ServiceImpl
 * @author : sehui
 * @fileName : ItemImgServiceImpl
 * @since : 250709
 * @history
 *  - 250716 | sehui | 상품 이미지 등록 기능 추가
 *  - 250716 | sehui | 상품 이미지 수정 기능 추가
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemImgServiceImpl implements ItemImgService {

    private final ItemImgMapper itemImgMapper;
    private final FileService fileService;

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    //상품 이미지 DB 등록
    @Override
    public void saveItemImg(ItemImgVO itemImgVO, MultipartFile itemImgFile) throws Exception {

        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        //ItemImgVo에 값 저장
        itemImgVO.setOriImgName(oriImgName);
        itemImgVO.setImgName(imgName);
        itemImgVO.setImgUrl(imgUrl);

        //상품 이미지 정보 저장
        itemImgMapper.insertItemImg(itemImgVO);
    }

    //상품 이미지 수정
    @Override
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {

        if(!itemImgFile.isEmpty()) {
        }

    }
}
