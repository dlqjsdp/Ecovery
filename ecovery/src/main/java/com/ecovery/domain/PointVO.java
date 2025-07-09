package com.ecovery.domain;

/*
 * 회원 포인트 VO
 * 회원별 포인트 정보
 * DB의 point 테이블과 매핑됩니다.
 * 작성자 : 오세희
 */

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PointVO {

    private Long pointId;
    private Long memberId;
    private int point;
    private Date lastModifiedAt;

}
