package com.example.shop.entity;

import com.example.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name ="orders") //정렬할 때 사용하는 order 키워드와 헷갈릴 수 있어 s를 붙임
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //한 명의 회원은 여러번 주문할 수 있어 주문 엔티티 기준에서 다대일 단방향 매핑 진행
    @JoinColumn(name = "memder_id")
    private Member member;

    //Order 엔티티에 OrderItem과 연관 관계 매핑 추가
    //단방향일 경우에는 테이블을 조회할 일이 없을 경우 제외해도 무관함
    //mappedBy : 나는 외래키가 아나예요 외래키는 order 변수명을 가지고 있어요 + 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeTypeALL 옵션 설정
    //cascade = CascadeType.ALL : 게시글 삭제 시 댓글도 삭제하겠다 - 영속성 전이
    // orphanRemoval = true : 고아 객체를 제거하기 위해 기재
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude //ToString 어노테이션 제거한다
    private List<OrderItem> orderItems = new ArrayList<>(); //여러 건을 담아 보여주기 때문에 List에 담는다(주문을 여러건 할 수 있으니까)

    private LocalDateTime orderDate; //주문일
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태(취소, 주문완료 등)
    
//    private LocalDateTime regTime; //최초 주문 시간 -> BaseEntity 상속으로 자동 반영
//    private  LocalDateTime updateTime; //주문 내용 수정 시간 -> BaseEntity 상속으로 자동 반영


}
