package com.ecovery.service;


import com.ecovery.domain.ItemImgVO;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.ItemFormDto;
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
 *  - 250717 | sehui | 상품 이미지 수정 기능 추가
 *  - 250724 | sehui | 상품 전체 이미지 조회 기능 추가
 *  - 250724 | sehui | 상품 이미지 단건 조회 기능 추가
 *  - 250724 | sehui | 대표 이미지 조회 Test 추가
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemImgServiceImpl implements ItemImgService {

    private final ItemImgMapper itemImgMapper;
    private final FileService fileService;

    @Value("C:/ecovery/item")
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
    public boolean updateItemImg(ItemImgVO itemImgVO, MultipartFile itemImgFile) throws Exception {

        int updateRows = 0;

        //기존 상품 이미지 조회
        if(!itemImgFile.isEmpty()) {
            ItemImgVO savedItemImg = itemImgMapper.getItemImgById(itemImgVO.getItemImgId());

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            //새로운 이미지 업로드
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;

            //이미지 정보 수정한 VO 객체
            itemImgVO.setOriImgName(oriImgName);
            itemImgVO.setImgName(imgName);
            itemImgVO.setImgUrl(imgUrl);

            //DB에 이미지 정보 수정 요청
            updateRows = itemImgMapper.updateItemImg(itemImgVO);
        }

        return updateRows == 1;
    }

    //상품 전체 이미지 조회
    @Override
    public List<ItemImgVO> getItemImgList(Long itemId) {
        return itemImgMapper.getItemImgList(itemId);
    }

    //상품 이미지 단건 조회
    @Override
    public ItemImgVO getItemImg(Long itemImgId) {
        return itemImgMapper.getItemImgById(itemImgId);
    }

    //대표 이미지 조회
    @Override
    public ItemImgVO getRepImgByItemId(Long itemId) {
        return itemImgMapper.findRepImgByItemId(itemId);
    }
}
