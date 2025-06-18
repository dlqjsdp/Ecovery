package com.example.firstproject.entity;

import com.example.firstproject.dto.CommentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity // 클래스 필드를 바탕으로 DB에 테이블 생성 // 반드시 기본키 필요
@Getter
@ToString
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
public class Comment {

    @Id // 기본키, 프라이버리 키 자동 생성
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 번호 자동 증가
    private Long id; // 대표키

 //   @OneToMany : 하나의 엔티티가 여러 개의 다른 엔티티와 연결된 관계
    // fetch = FetchType.LAZY를 기재하지 않으면 댓글 삭제 시 Article 테이블도 같이 확인되어 JOIN됨, 기재 하면 Comment 테이블만 확인하고 액션 진행
    // LAZY : 내 테이블만 확인
    @ManyToOne(fetch = FetchType.LAZY) // Comment 엔티티와 Article 엔티티를 다대일 관계로 설정 // JPA에서 외래키 자동 생성(기본 규칙으로 article_id 생성)
    @JoinColumn(name="article_id") // 없어도 됨 > 자동 생성된 외래키 이름 변경하는 용도
    private Article article; // 해당 댓글의 부모 게시글, 외래키 설정해줌
    private String nickname; // 댓글을 단 사람
    private String body; // 댓글 본문

    // Comment 엔티티의 생성 메서드 -> 엔티티를 생성할 수 없는 경우 예외를 발생시키고 그렇지 않으면 엔티티 생성 후 반환
    public static Comment create(CommentDto dto, Article article) {
        // 1. 예외발생
        if (dto.getId() != null) { // dto에 id가 존재하는 경우 -> 자동 생성되기 때문에
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 id가 없어야 합니다.");
        }
        if (dto.getArticleId() != article.getId()) { // 게시글이 일치 하지 않는 경우 -> dto에서 가져온 부모게시글과 엔티티에서 가져온 부모 게시글 id가 다를 경우
            throw new IllegalArgumentException("댓글 생성 실패! 게시글의 id가 잘못됐습니다.");
        }
        // 2. 엔티티 생성 및 반환(전달값으로 필요한 요소를 담아서)
        return new Comment( 
                dto.getId(), // 댓글 아이디
                article,    // 부모 게시글
                dto.getNickname(), // 댓글 닉네임
                dto.getBody()       // 댓글 본문
        );
    }

    // Comment 엔티티의 patch 메서드 만들기
    public void patch(CommentDto dto) {
        // 1. 예외 발생
        if (this.id != dto.getId()) {
            throw new IllegalArgumentException("댓글 수정 실패! 잘못된 id가 입력됐습니다.");
        }
        // 2. 객체 갱신
        if (dto.getNickname() != null) { // 수정할 nickname 있니?
            // this는 실행 중인 객체 자신을 의미함 : 수정이 대상이 되는 Comment 객체, 즉 DB에서 불러온 기존 댓글 객체(target)
            this.nickname = dto.getNickname(); // 내용 반영
        }
        if (dto.getBody() != null) { // 수정한 데이터 있니?
            this.body = dto.getBody(); // 내용 반영
        }
    }
}
