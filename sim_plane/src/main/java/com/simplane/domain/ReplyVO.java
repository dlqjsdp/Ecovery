package com.simplane.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyVO {

    private Long replyid;
    private Long testid;
    private String reply;
    private Date replydate;
}
