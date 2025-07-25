package com.ecovery.service;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.domain.ItemImgVO;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.mapper.FreeImgMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

/*
 * 무료나눔 이미지 서비스 구현체
 * - 무료나눔 게시글에 연결된 이미지의 등록, 조회, 수정, 삭제 기능을 담당
 * - 실제 이미지 파일은 서버에 저장하고, DB에는 파일명과 경로 정보를 저장
 * - 게시글 삭제 시 DB는 외래키 ON DELETE CASCADE로 이미지 자동 삭제 처리
 * - 파일 삭제는 직접 수행 (서버의 실제 이미지 파일 제거)
 *
 * @author : yeonsu
 * @fileName : FreeImgServiceImpl
 * @since : 250721
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeImgServiceImpl implements FreeImgService {


    private final FreeImgMapper freeImgMapper;
    private final FileService fileService;

    // application.properties에서 불러오기
    @Value("${freeImgLocation}")
    private String freeImgLocation;

    /*
    * 이미지 등록
    * 파일명을 UUID로 저장하고, DB에는 경로와 원본명 함께 저장
    */
    @Override
    public void saveFreeImg(FreeImgVO freeImgVO, MultipartFile freeImgFile) throws Exception {
        String oriImgName = freeImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(freeImgLocation, oriImgName, freeImgFile.getBytes());
            imgUrl = "/images/free/" + imgName;
        }

        freeImgVO.setOriImgName(oriImgName);
        freeImgVO.setImgName(imgName);
        freeImgVO.setImgUrl(imgUrl);

        freeImgMapper.insert(freeImgVO);
    }

    /*
     * 이미지 수정
     * 기존 파일 삭제 후, 새로운 파일로 업데이트
     */
    @Override
    public boolean updateFreeImg(FreeImgVO freeImgVO, MultipartFile freeImgFile) throws Exception {
        if (freeImgFile.isEmpty()) {
            return false;
        }

        // 기존 이미지 조회
        FreeImgDto savedImg = freeImgMapper.getById(freeImgVO.getFreeImgId());

        // 기존 이미지 삭제
        if (!StringUtils.isEmpty(savedImg.getImgName())) {
            fileService.deleteFile(freeImgLocation + "/" + savedImg.getImgName());
        }

        // 새 이미지 저장
        String oriImgName = freeImgFile.getOriginalFilename();
        String imgName = fileService.uploadFile(freeImgLocation, oriImgName, freeImgFile.getBytes());
        String imgUrl = "/images/free/" + imgName;

        // VO 업데이트
        freeImgVO.setOriImgName(oriImgName);
        freeImgVO.setImgName(imgName);
        freeImgVO.setImgUrl(imgUrl);

        int result = freeImgMapper.update(freeImgVO);
        return result == 1;
    }
}

