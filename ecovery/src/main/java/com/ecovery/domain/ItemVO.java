package com.ecovery.domain;

import com.ecovery.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 에코마켓 상품 VO
 * 에코마켓에서 판매하는 상품의 정보
 * DB의 item 테이블과 매핑됩니다.
 * 작성자 : 오세희
 */

@Getter
@Setter
public class ItemVO {

    private Long itemId;
    private String itemName;
    private int price;
    private int stockNumber;
    private String itemDetail;
    private ItemSellStatus itemSellStatus;
    private LocalDateTime createdAt;
}
