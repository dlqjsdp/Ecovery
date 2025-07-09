CREATE DATABASE ecovery;		-- 데이터베이스 생성
USE ecovery;

-- free 무료나눔 게시글 테이블
CREATE TABLE free (
                      free_id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- 게시글 고유번호 (PK)
                      member_id BIGINT NOT NULL,                      -- 작성자 회원 ID (FK, member.member_id)
                      title VARCHAR(200) NOT NULL,                    -- 게시글 제목
                      content VARCHAR(1000) NOT NULL,                 -- 게시글 상세 내용 (최대 1000자)
                      category VARCHAR(20) NOT NULL,                  -- 품목 카테고리 (가구/가전/잡화/기타)
                      region_gu VARCHAR(50) NOT NULL,                 -- 나눔 지역 - 구 (예 : 강서구)
                      region_dong VARCHAR(50) NOT NULL,               -- 나눔 지역 - 동 (예 : 화곡동)
                      item_condition VARCHAR(10) NOT NULL DEFAULT '상', -- 상품 상태 (상/중/하)
                      deal_status VARCHAR(10) NOT NULL DEFAULT '진행중', -- 거래 상태(진행중, 완료)
                      view_count INT NOT NULL DEFAULT 0,               -- 게시글 조회수
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 게시글 등록일 (수정 시 갱신)

                      FOREIGN KEY (member_id) REFERENCES member(member_id) -- ✅ 외래키 설정
);

-- free_img 무료나눔 이미지 테이블
CREATE TABLE free_img (
                          free_img_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 무료나눔 이미지 고유번호(PK)
                          free_id BIGINT NOT NULL,                        -- 이미지가 속한 게시글의 ID (FK, free.free_id)
                          img_name VARCHAR(255) NOT NULL,                 -- 서버에 저장된 이미지 파일명
                          ori_img_name VARCHAR(255),                      -- 사용자가 업로드한 원본 파일명check
                          img_url VARCHAR(500) NOT NULL,                  -- 이미지 접근 경로(서버 url 등)
                          repimg_yn CHAR(1) NOT NULL,                     -- 대표 이미지 여부(예: 'Y'/'N')
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 이미지 등록일(수정 시 갱신)

                          FOREIGN KEY (free_id) REFERENCES free(free_id)  -- 외래키 설정
);

-- free_reply 무료나눔 게시글 댓글 테이블
CREATE TABLE free_reply (
                            reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 댓글ID
                            free_id BIGINT NOT NULL,                      -- 댓글이 속한 게시글 ID (FK, free.free_id)
                            member_id BIGINT NOT NULL,                    -- 댓글 작성자 ID (FK, member.member_id)
                            content VARCHAR(1000) NOT NULL,               -- 댓글 본문 내용
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 댓글 작성일시(수정 시 갱신)

                            FOREIGN KEY (free_id) REFERENCES free(free_id),           -- 외래키: 게시글 ID
                            FOREIGN KEY (member_id) REFERENCES member(member_id)      -- 외래키: 회원 ID
);



-- member 회원 테이블
CREATE TABLE member (
                        member_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 회원 고유번호(PK)
                        email VARCHAR(255) NOT NULL UNIQUE, -- 로그인용 이메일 (중복불가)
                        password VARCHAR(255) NOT NULL, -- 비밀번호(해시 저장 권장)
                        nickname VARCHAR(50) NOT NULL, -- 닉네임
                        role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER', -- 권한 (기본user)
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);