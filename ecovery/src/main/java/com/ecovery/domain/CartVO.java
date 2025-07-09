package com.ecovery.domain;

/*
 * 회원의 장바구니 VO
 * 에코마켓 이용하는 회원의 장바구니 정보
 * DB의 cart 테이블과 매핑됩니다.
 * 작성자 : 오세희
 */

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CartVO {

    private Long cartId;
    private Long memberid;
    private Date createdAt;
}
