package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import com.example.shop.repository.OrderItemRepository;
import com.example.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class OrderTest {

    @Autowired
    OrderRepository orderRepository; //주문 엔티티와 관련된 데이터를 DB에 저장하거나 불러오는 역할

    @Autowired
    ItemRepository itemRepository; //상품 엔티티의 DB 처리를 담당하는 리포지토리 -> 상품을 저장할 때 사용

    @PersistenceContext
    EntityManager em; //EntityManager를 직접 주입

    @Autowired
    MemberRepository memberRepository; //회원 엔티티를 저장하거나 조회하는데 사용

    @Autowired
    OrderItemRepository orderItemRepository; //주문 상품 정보를 다루는데 사용

    //OrderItem이 참조하는 Order 객체가 실제로 지연 로딩 방식으로 로딩되느닞 확인하는 테스트
    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder(); //테스트용 주문 생성

        Long OrderItemId = order.getOrderItems().get(0).getId(); //첫번째 OrderItem의 ID 추출

        log.info("orderItemId ==> {}", OrderItemId);
        
        em.flush(); //영속성 컨텍스트 초기화 -> 실제 DB에서 조회되도록 강제(영속성은 DB 방문 전에 데이터가 있는지 1차 보는 공간)
        em.clear();

        // clear 이후 조회하는거라 영속성 컨텍스트 캐시를 거치지 않고 실제 DB에서 쿼리를 날림
        OrderItem orderItem = orderItemRepository.findById(OrderItemId) //DB에서 OrderItemId의 OrderItem 찾아서
                .orElseThrow(() -> new EntityNotFoundException("ID값 없음")); //만약 없으면 예외 던짐

        //조회시 실제 클래스는 orderItem, order만 필요
        log.info("orderItem class ==> {}", orderItem);
        log.info("order class ==> {}", orderItem.getOrder().getClass()); //참조하고 있는 테이블에 정보도 가져온다(JOIN)
    }
    //테스트용 주문 생성 메서드
    public Order createOrder() {
        Order order = new Order(); //주문 객체 생성

        for(int i=0; i<3; i++){ //Item, OrderItem 3개 생성 -> 3개의 상품과 주문 항목 생성
            Item item = createItem(); //createItem 함수로 Item 객체 생성하여
            itemRepository.save(item); //상품 저장

            //OrderItrm이 어떤 상품을 몇 개, 얼마에 주문했는지 설정
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item); //주문 항목에 상품 연결
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem); //영속성 컨텍스트에 저장되지 않는 orderItem 엔티티를 order 엔티티에 담아준다
        }

        Member member = new Member(); //Member 1명 생성
        memberRepository.save(member); //DB 저장

        order.setMember(member); //order 1개 생성
        orderRepository.save(order); //DB 저장

        return order;
    }

    //Order에서 OrderItem을 제거하면 DB에서도 삭제되는지 확인하는 테스트
    @Test
    @DisplayName("고아객체 제거 테스트")
    public void  orphanRemovalTest(){
        Order order = this.createOrder(); //주문 생성 후

        order.getOrderItems().remove(0); //OrderItem 하나를 리스트에서 제거

        em.flush(); //변경된 내용 DB 반영
    }

    public Item createItem() {

        Item item = new Item(); //상품 객체 생성

        item.setItemNm("테스트 상품");
        item.setPrice(12900);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        return item;
    }

    //Order 객체만 저장했을 때, 내부의 OrderItem들도 자동으로 함께 저장되는지 확인 테스트 - 영속성 전이 테스트
    @Test
    public void cascadeTest(){
        Order order = new Order();

        //3개의 OrderItem 생성 및 설정
        for(int i=0; i<3; i++){
            Item item = this.createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item); 
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem); //order 클래스에서 리스트에 담는 코드 참고
        }

        orderRepository.save(order); //오직 order만 저장(연관되어 있는 OrderItem을 수동으로 저장하지 않고 order에만 추가)

        em.flush();
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new EntityNotFoundException("ID 없음"));
        assertEquals(3, savedOrder.getOrderItems().size());
    }

}