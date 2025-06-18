package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByitemNm(String itemNm);

    List<Item> findByitemNmLike(String itemNm);

    List<Item> findByPriceLessThan(int price);

    //JPQL -> entity 객체 이용(* 인식을 못하므로 애칭 필수)
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")  //i는 애칭
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //nativeQuery -> db에 있는 table 이용
    @Query(value="select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery=true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
