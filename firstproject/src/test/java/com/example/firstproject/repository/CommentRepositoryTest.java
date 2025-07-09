package com.example.firstproject.repository;

import com.example.firstproject.entity.Comment;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository comment;

    @Test
    void findByArticleId() {

        comment.findByArticleId(4L)
        .forEach(System.out::println);
    }

    @Test
    @DisplayName("닉네임으로 댓글 조회")
    void findByNickname() {

        comment.findByNickname("Kim")
                .forEach(System.out::println);
    }
}