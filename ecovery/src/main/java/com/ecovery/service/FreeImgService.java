package com.ecovery.service;


/*
 * 무료나눔 이미지 서비스 인터페이스
 * 실제 구현은 FreeImgServiceImpl에서 처리
 *
 * @author : yeonsu
 * @fileName : FreeImgService
 * @since : 250721
 */


import com.ecovery.domain.FreeImgVO;
import com.ecovery.dto.FreeImgDto;

import java.util.List;

public interface FreeImgService {

    public void register(FreeImgVO imgVO); // 이미지 등록

    public List<FreeImgDto> getAll(Long freeId); // 게시글에 연결된 이미지 전체 조회

    public boolean modify(FreeImgVO imgVO); // 이미지 수정

    public void removeAll(Long freeId); // 게시글에 연결된 이미지 전체 삭제

    public boolean remove(Long freeImgId); // 특정 이미지 한 장 삭제

    FreeImgDto getById(Long freeImgId); // 이미지 1장 조회 (free_img_id 기준)
}
