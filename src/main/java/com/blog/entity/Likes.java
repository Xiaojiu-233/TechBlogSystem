package com.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

//点赞
@Data
public class Likes {
    private Long id;//id

    private Long userId;//来源用户id

    private Integer states;//是否点赞 1点赞 0未点赞
}
