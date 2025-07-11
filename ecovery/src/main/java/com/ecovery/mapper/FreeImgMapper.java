package com.ecovery.mapper;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.dto.FreeImgDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 무료나눔 이미지 Mapper
 * 무료나눔 게시글과 연결된 이미지 DB 연동을 위한 인터페이스
 * @author : yeonsu
 * @fileName : FreeImgMapper
 * @since : 250711
 */

@Mapper
public interface FreeImgMapper {

    public void insert(FreeImgVO freeImgVO);             // 이미지 1장 등록

    public int update(FreeImgVO freeImgVO);              // 이미지 수정시 이미지 교체

    public List<FreeImgDto> getFreeImgList(Long freeId); // 게시글에 연결된 전체 이미지 조회

    public int deleteByUuid(String uuid);                // UUID 기준으로 이미지 1건 삭제(성공시 1, 실패시 0)

    public int deleteByFreeId(Long freeId);              // 게시글에 연결된 이미지 전체 삭제(성공시 1, 실패시 0)
                                                         // (ex.게시글 삭제시, 이미지 5장 삭제시 return = 5)

    public FreeImgDto getRepImg(Long freeId);            //



}
