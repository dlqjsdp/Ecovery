package com.ecovery.service;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeReplyDto;
import com.ecovery.mapper.FreeReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 무료나눔 댓글 서비스 구현 클래스
 * FreeReplyMapper를 활용한 댓글 비즈니스 로직 처리
 *
 * 댓글 및 대댓글 등록, 조회, 수정, 삭제, 목록 조회, 수 카운트 기능 포함
 * Controller → Service → Mapper → DB 순으로 동작
 *
 * @author : yeonsu
 * @fileName : FreeReplyServiceImpl
 * @since : 250724
 */


@Service
@RequiredArgsConstructor
@Slf4j
public class FreeReplyServiceImpl implements FreeReplyService {

    private final FreeReplyMapper freeReplyMapper;

    // 댓글 등록
    @Override
    public void register(FreeReplyVO freeReply) {
        log.info("댓글 등록 요청: {}", freeReply);
        freeReplyMapper.insert(freeReply);
    }

    // 댓글 단건 조회
    @Override
    public FreeReplyDto get(Long replyId) {
        log.info("댓글 조회 요청: {}", replyId);
        return freeReplyMapper.read(replyId);
    }

    // 댓글 수정
    @Override
    public boolean modify(FreeReplyVO freeReply) {
        log.info("댓글 수정 요청: {}", freeReply);
        return freeReplyMapper.update(freeReply) == 1;
    }

    // 댓글 삭제
    @Override
    public boolean remove(Long replyId) {
        log.info("댓글 삭제 요청: {}", replyId);
        return freeReplyMapper.delete(replyId) == 1;
    }

    // 부모 댓글 목록 조회
    @Override
    public List<FreeReplyDto> getParentReplies(Long freeId, Criteria criteria, String sortType) {
        log.info("부모 댓글 목록 조회: freeId={}, sortType={}", freeId, sortType);
        return freeReplyMapper.getParentReplies(freeId, criteria, sortType);
    }

    // 대댓글 목록 조회
    @Override
    public List<FreeReplyDto> getChildReplies(Long parentId) {
        log.info("대댓글 목록 조회: parentId={}", parentId);
        return freeReplyMapper.getChildReplies(parentId);
    }

    // 전체 댓글 수 조회
    @Override
    public int getTotalReplyCount(Long freeId) {
        log.info("댓글 수 조회: freeId={}", freeId);
        return freeReplyMapper.getTotalReplyCount(freeId);
    }

    // 특정 댓글의 대댓글 수 조회
    @Override
    public int getChildReplyCount(Long parentId) {
        log.info("대댓글 수 조회: parentId={}", parentId);
        return freeReplyMapper.getChildReplyCount(parentId);
    }
}
