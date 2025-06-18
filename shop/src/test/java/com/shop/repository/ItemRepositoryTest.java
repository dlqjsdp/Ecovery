package com.shop.repository;

import com.shop.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {

        for(int i=0; i<10; i++){

            Item item = new Item();

            item.setItemNm("테스트 상품");
            item.setPrice(10000);
            item.setItemDetail("테스트 상품 상세 설명");
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item. setUpdateTime(LocalDateTime.now());

            Item savedItem = itemRepository.save(item);

            log.info("savedItem : {}", savedItem.toString());
        }
    }

    public void createItemList() {

        for(int i=1; i<11; i++){

            Item item = new Item();

            item.setItemNm("테스트 상품 " + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명 " + i);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item. setUpdateTime(LocalDateTime.now());

            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findItemNmTest() {
        this.createItemList();

        List<Item> itemList = itemRepository.findByitemNm("테스트 상품 1");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("상품명 LIKE 조회 테스트")
    public void findItemNmLikeTest() {
        this.createItemList();

        List<Item> itemList = itemRepository.findByitemNmLike("%1%");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("상품 가격 조회 테스트")
    public void findItemNmLessThanTest() {
        this.createItemList();

        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findItemDetailTest() {
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemDetail("설명1");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findItemDetailByNativeTest() {
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemDetailByNative("설명 1");
        itemList.forEach(item -> log.info("item : {}", item.toString()));
    }
}