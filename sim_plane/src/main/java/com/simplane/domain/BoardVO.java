package com.simplane.domain;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardVO {

    private Long boardid;
    private String title;
    private String content;
    private Date regDate;
    private String imagePath;
}

