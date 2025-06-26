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
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();

        return savedFileName;
    }

    //저장된 파일을 삭제하는 메서드
    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);
        
        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        }else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
