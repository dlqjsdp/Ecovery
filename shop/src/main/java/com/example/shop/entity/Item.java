package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// 상품 정보를 저장할 JPA 엔티티 클래스

@Entity //JPA에서 관리되는 엔티티
@Table(name="item") // DB테이블 이름 item으로 지정
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id //기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY: 데이터베이스 자체에서 기본키를 생성하도록 위힘
    @Column(name = "item_id")
//  @GeneratedValue(strategy = GenerationType.AUTO) // AUTO : JPA가 사용 중인 DB에 따라 자동으로 전략 선택
    private Long id; // 상품 코드

    @Column(nullable=false, length = 50) //nullable=false : null 값을 허용하지 않겠다
    private String itemNm; // 상품명

    @Column(nullable=false, name = "price")
    private int price; // 가격

    @Column(nullable=false)
    private int stockNumber; // 재고 수량

    @Lob //대용량 텍스트 저장
    @Column(nullable=false)
    private String itemDetail; // 상품 상세 설명

    @Enumerated(EnumType.STRING) //필수기재 : DB에 sell 또는 sold_out으로 저장됨 // 참고로 ORDINAL은 번호로 저장되니 절대 사용하지 않는게 좋음
    private ItemSellStatus itemSellStatus; // 상품 판매 상태

//    private LocalDateTime regTime; // 등록 시간 -> BaseEntity 상속으로 자동 반영
//    private LocalDateTime updateTime; // 수정 시간 -> BaseEntity 상속으로 자동 반영

    //상품 데이터 업데이트 - 엔티티가 아닌 DTO에서 하는게 나음 엔티티는 순수하게 엔티티만 기재하는게 좋음
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

}
