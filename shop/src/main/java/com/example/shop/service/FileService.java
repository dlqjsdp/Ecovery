package com.example.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

//파일은 업로드하고 삭제하는 기능을 담당하는 서비스 클래스

@Service
@Slf4j
public class FileService {

    //사용자가 업로드한 파일을 서버에 저장하는 메소드
    //uploadPath : 파일 경로, originalFileName : 사용자가 업로드한 원본 파일 이름, fileData : 업로드된 파일 내용
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID(); //UUID: 중복되지 않는 랜덤 문자열 생성 (파일명 충돌 방지용)

        //sampletest.jsp - 확장자 분리
        //이미지 파일명에서 마지막 . 위치를 찾아 확장자만 잘라냄(.jsp, .png 등)
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String savedFileName = uuid.toString() + extension; //저장할 파일명 생성(abc... .jsp)
        String fileUploadFullUrl = uploadPath + "/" + savedFileName; //실제 저장경로 만들기(전체 경로 만들기) // uploadPath 경로에 새로 생성한 파일명으로 지정

        //파일 저장
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); //FileOutputStream : 실제 하드디스크에 파일을 쓰기 위한 스트림
        fos.write(fileData); //파일 내용 기록
        fos.close(); //스트림 닫기

        return savedFileName; //DB에 저장할 파일명 반환
    }

    //저장된 파일을 삭제하는 메서드
    public void deleteFile(String filePath) throws Exception { //filePath : 삭제할 파일의 전체 경로
        File deleteFile = new File(filePath); //삭제할 파일을 가리키는 객체 생성
        
        if(deleteFile.exists()) { //파일이 존재하면
            deleteFile.delete(); //삭제
            log.info("파일을 삭제하였습니다.");
        }else { //아니면
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
