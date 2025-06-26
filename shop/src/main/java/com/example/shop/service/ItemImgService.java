package com.example.shop.service;

import com.example.shop.entity.ItemImg;
import com.example.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;

/*
    == 상품 이미지 업로드와 DB를 저장하는 핵심 서비스 클래스 ==
    클래스명 : ItemImgService
    작성자 : 방희경
    작성일자 : 2025-06-25
    설명 : 상품이미지 업로드 하고, 상품 이미지 정보를 저장하는 서비스(업로드한 이미지 파일을 서버에서 저장 + 그 이미지 정보를 DB 테이블에 저장)
*/

@Service
@Slf4j
@RequiredArgsConstructor //생성자 주입 방식으로 final 필드 초기화
@Transactional
public class ItemImgService {

    //생성자 주입을 위한 의존성 필드 선언(클래스의 역할에 따라 필드 선언 종류가 달라진다)
    private final ItemImgRepository itemImgRepository; //상품 이미지 정보를 DB에 저장.조회하는 레파지토리
    private final FileService fileService; //실제 파일을 업로드하는 기능을 가진 서비스

    //이미지 저장 경로(C:/shop/item)
    @Value("${itemImgLocation}") //application.yml에 있는 설정 값을 주입받기 위해 기재
    private String itemImgLocation; //itemImgLocation 설정 값을 읽어 itemImgLocation 변수에 자동으로 대입

    // itemImgFile.getBytes()는 파일의 바이트 데이터를 가져오는 메서드로 입출력 예외가 발생될 수 있어 예외 처리 진행
    //상품 이미지 저장 메서드 - 클라이언트가 업로드한 이미지 파일을 서버에 저장하고 이미지 정보를 DB에 저장하는 기능
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception { //throws : 예외처리

        //사용자가 업로드한 원본 파일명
        String oriImgName = itemImgFile.getOriginalFilename(); //이미지 저장
        String imgName = ""; //서버에 저장될 이미지 파일명
        String imgUrl = ""; //웹 브라우저에서 접근할 수 있는 이미지 url

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)) { //원 파일명이 있을경우 업로드 진행
            // uploadFile 메서드 호출하여 서버 디스크에 실제 파일을 저장하는 로직
            //필요한 정보 : itemImgLocation(어디에 저장할껀지?) + oriImgName(업로드한 파일 이름이 뭔지) + itemImgFile.getBytes()(실제 파일 내용을 바이트로 변환하여)
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 사용자가 접근 가능한 URL 경로 생성
            imgUrl = "/images/item/" + imgName;
        }

        //상품 이미지 정보 저장 - DB에 저장할 ItemImg 세팅
        itemImg.updateItemImg(oriImgName, imgName, imgUrl); // 엔티티 객체에 이미지 정보(원본이름, 저장이름, URL)를 세딩
        itemImgRepository.save(itemImg); //DB 저장
    }

    //이미지 수정
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {

        log.info("=============== : {}" + itemImgId);

        if(!itemImgFile.isEmpty()) {
            //개별적 핸들링 -> 이미지는 여러장인데 List로 받지 않고 savedItemImg 받는 이유를 생각해보아야한다 : 이건 item_id는 여러건이나 itemImgId 사진마다 주어진 id는 1건임으로 리스트로 받지 않음
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(() -> new EntityNotFoundException());

            //기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile((itemImgLocation + "/" + savedItemImg.getImgName()));
            }
            //신규 이미지 저장
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); //실제 저장 코드
            String imgUrl = "/images/item/" + imgName;

            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
