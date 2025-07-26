package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//장바구니 엔티티 설계

@Entity //DB 테이블과 매핑되는 객체
@Table(name = "cart") //DB 테이블 이름 cart
@Getter
@Setter
@ToString
public class Cart extends BaseEntity {

    @Id //기본키
    @Column(name = "cart_id") //DB의 컬럼 이름 cart_id를 명시적으로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA에서 기본키(PK)를 자동으로 생성하는 방식을 지정
    private Long id;

    //멤버엔티티와 장바구니엔티티는 일대일 매핑
    //장바구니 엔티티가 일방적으로 회원 엔티티를 참조하고 있어 단방향 매핑
    @OneToOne(fetch = FetchType.LAZY) //일대일 단방향 매핑
    @JoinColumn(name = "member_id") //Cart 테이블에 member_id라는 외래키 컬럼을 생성해서, Member 테이블의 id를 참조
    private Member member;
}
