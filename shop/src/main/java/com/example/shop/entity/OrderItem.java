package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

//다대다 관계를 1:N, N:1로 쪼개서 만든 중간 엔티티(CartItem이 둘 사이를 연결해주는 중간다리)
//하나의 Order(주문) 안에 여러 개의 Item(상품)을 담을 수 있음
//하나의 Item(상품)은 → 여러 번 주문될 수 있음

@Entity
@Getter
@Setter
@ToString
@Table(name ="order_item")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    //(fetch = FetchType.LAZY) : 지연로딩
    @ManyToOne(fetch = FetchType.LAZY) //하나의 상품을 여러개 주문할 수 있음으로 주문 상품 기준으로 다대일 단방향 매핑 설정
    @JoinColumn(name = "item_id") //item_id 외래키 설정 Item 클래스와 연결되어 있음
    private Item item;

    //외래키 설정 - 1:N의 경우 N에 외래키를 설정해야함 -> order 변수명으로 외래키 설정
    @ManyToOne(fetch = FetchType.LAZY) //한 번의 주문에 여러 개의 상품을 주문 할 수 있음으로 주문 상품 엔티티와 주문 엔티티를 다대일 단방향 매핑 설정
    @JoinColumn(name = "order_id") //외래키 이름은 order_id
    private Order order;

    //주문 상세 정보
    private int orderPrice; //주문가격
    private int count; //주문수량
//    private LocalDateTime regTime; //최초 주문 시간 -> BaseTimeEntity로 인해 자동으로 반영
 //   private  LocalDateTime updateTime; //주문 내용 수정 시간 -> BaseTimeEntity로 인해 자동으로 반영
}
