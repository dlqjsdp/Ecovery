package com.example.shop.repository;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository; // 테스트 클래스 안에서 itemRepository 자동 주입하여 사용 가능

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item(); // Item 객체를 새로 생성

        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item); //save 메서드를 이용하여 DB에 item 객체를 저장하고 saveItem이라는 변수로 다시 받아옴

        log.info("savedItem : {}", savedItem);
    }
    
    // 상품 객체 10개를 데이터베이스에 저장하는 테스트 메서드
    public void createItemListTest() {
        for (int i=1; i<=10; i++) {

            Item item = new Item(); //빈 Item 객체 생성

            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());

            Item savedItem = itemRepository.save(item); // 데이터베이스에 저장
        }
    }

    // 상품명이 일치하는지 상품조회  테스트 - 정확하게 일치하는 상품명만 조회 가능
    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        this.createItemListTest();

        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    // itemNm이 주어진 문자열과 유사한 값을 가진 상품 조회 : 매개변수에 %를 직접 포함하여 넘겨줘야 함
    // %테스트 상품1% 기재 시 1건 죄회, %테스트 상픔%으로 조회 시 자동생성된 10개 조회 됨
    @Test
    @DisplayName("상품명 Like 조회 테스트")
    public void findByItemNmLikeTest() {
        this.createItemListTest();

        List<Item> itemList = itemRepository.findByItemNmLike("%테스트 상품1%");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    // 전달한 price보다 작은 가격의 상품 목록 조회
    @Test
    @DisplayName("상품명 PriceLessThan 조회 테스트")
    public void findByPriceLessThanTest() {
        this.createItemListTest();

        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    // @Query를 사용해서 직접 SQL을 작성한 예제 - JPQL 방식 : JPQL -> entity 객체 이용
    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest() {
        this.createItemListTest();

        List<Item> itemList = itemRepository.findByItemDetail("설명1");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    // @Query를 사용해서 직접 SQL을 작성한 예제 - Native SQL 방식 : navtivQuery -> db에 있는 table 이용
    @Test
    @DisplayName("@Query Native를 이용한 상품 조회 테스트")
    public void findByItemDetailNativeTest() {
        this.createItemListTest();

        List<Item> itemList = itemRepository.findByItemDetailByNative("설명1");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

}