package com.ecovery.domain;

import com.ecovery.constant.ItemSellStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 에코마켓 상품 VO
 * @author : sehui
 * @fileName : ItemVO
 * @since : 250709
 */

@Getter
@Setter
@ToString
@Builder
public class ItemVO {

    private Long itemId;
    private String itemName;
    private int price;
    private int stockNumber;
    private String category;
    private String itemDetail;
    private ItemSellStatus itemSellStatus;
    private LocalDateTime createdAt;
}
