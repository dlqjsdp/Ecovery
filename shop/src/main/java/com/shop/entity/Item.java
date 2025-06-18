package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter
@Setter  //entity에 setter는 좋은 건 아님. 테스트를 위해 넣음
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(nullable = false, name="price")
    private int price; //상품 가격

    @Column(nullable = false)
    private int stockNumber; //재고 수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    //타입이 enum이므로 enum 내 객체만 쓸 수 있음.
    @Enumerated(EnumType.STRING)  //STRING: enum의 이름을 db에 저장. ORDINAL: 순서를 db에 저장(쓰면 수정할 db 문제 생김)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    private LocalDateTime regTime; //등록 시간

    private LocalDateTime updateTime; //수정 시간
}
