package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//상품 등록 구현하기

@Entity
@Getter
@Setter
@Table(name = "item_img")
public class ItemImg extends BaseEntity {
    
    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //데이터베이스가 자동으로 숫자를 증가시켜 ID를 생성
    private Long id;
    
    private String imgName; //이미지 파일명

    private String oriImgName; //원본 이미지 명

    private String imgUrl; //이미지 조회 경로

    private String repimgYn; //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY) //여러 이미지가 하나의 상품에 속함으로 다대일 관계
    @JoinColumn(name = "item_id") //외래키 컬럼 item_id + 실제로 사용할 때까지 Item을 불러오지 않음(지연로등)
    private Item item;

    public void updateItemImg(String oriImgName, String imgName, String imgUrl) { //이미지 정보 변경에 필요한 핵심 데이터 3개를 이미지 정보 업데이트 한다
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
