package com.example.firstproject.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository comment;

    @Test
    void findByArticleId() {

        comment.findByArticleId(4L)
                .forEach(comment -> System.out.println(comment));
    //    .forEach(System.out::println); // 메서드 참조 방식
    }
}