package com.ecovery.service;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeReplyDto;

import java.util.List;

/*
 * 무료나눔 댓글 서비스 인터페이스
 * 댓글 및 대댓글 관련 기능 정의
 * 구현은 FreeReplyServiceImpl 클래스에서 처리
 *
 * @author : yeonsu
 * @fileName : FreeReplyService
 * @since : 250724
 */

public interface FreeReplyService {

    // 댓글 등록
    public void register(FreeReplyVO freeReply);

    // 댓글 단건 조회
    public FreeReplyDto get(Long replyId);

    // 댓글 수정
    public boolean modify(FreeReplyVO freeReply);

    // 댓글 삭제
    public boolean remove(Long replyId);

    // 게시글의 부모 댓글 목록 조회(페이징 + 정렬)
    List<FreeReplyDto> getParentReplies(Long freeId, Criteria criteria, String sortType);

    // 특정 부모 댓글의 대댓글 목록 조회
    List<FreeReplyDto> getChildReplies(Long parentId);

    // 특정 게시글의 전체 댓글 수
    public int getTotalReplyCount(Long freeId);

    // 특정 댓글의 대댓글 수
    public int getChildReplyCount(Long parentId);
}
