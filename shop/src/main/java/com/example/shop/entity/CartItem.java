package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//다대다 관계를 1:N, N:1로 쪼개서 만든 중간 엔티티(CartItem이 둘 사이를 연결해주는 중간다리)
//한 명의 사용자(Cart)는 여러 개의 상품(Item)을 장바구니에 담을 수 있음
//하나의 상품(Item)은 여러 사람(Cart)에 의해 장바구니에 담길 수 있음

@Entity
@Getter
@Setter
@ToString
@Table(name = "cart_item") //DB테이블 이름
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue //자동 증가 값 사용(기본 AUTO)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //한 장바구니에 여러 상품을 담을 수 있으니까
    @JoinColumn(name = "cart_id") //다른 테이블 Cart와 연결되어 있고 DB에 cart_id라는 이름으로 컬럼 생성
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY) //하나의 상품에 여러 장바구니에 담길 수 있으니까
    @JoinColumn(name = "item_id") //다른 테이블 Item와 연결되어 있고 DB에 item_id라는 이름으로 컬럼 생성
    private Item item;
    
    private int count; //카드안 상품 갯수

}
