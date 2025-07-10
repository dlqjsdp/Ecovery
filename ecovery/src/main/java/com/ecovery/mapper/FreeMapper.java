package com.ecovery.mapper;

import com.ecovery.dto.Criteria;
import com.ecovery.domain.FreeVO;
import com.ecovery.dto.FreeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FreeMapper {

    public List<FreeDTO> getFreeList(); // 게시글 전체 목록 조회

    public FreeVO read(Long freeId);// 게시글 상세 조회(게시글 번호로 조회)

    public void insert(FreeVO freeVO); // 게시글 등록

    public void insertSelectKey(FreeVO freeVO); // 게시글 등록 후 생성된 PK를 freeVO에 저장

    public int update(FreeVO freeVO); // 게시글 수정 (성공시 1, 실패시 0)

    public int delete(Long freeId); // 게시글 삭제 (성공시 1, 실패시 0)

    public List<FreeDTO> getListWithPaging(Criteria cri); // 게시글 목록 조회 (작성자 닉네임 포함)

    public int getTotalCount(Criteria cri); //페이징 처리를 위한 전체 게시글 수 조회

    public void updateViewCount(Long freeId); // 게시글 조회수 1 증가

    public FreeDTO readWithWriter(Long freeId); // 게시글 상세조회 + 작성자 닉네임 조회
}
