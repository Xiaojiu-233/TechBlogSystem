package com.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

//评论
@Data
public class Comment {
    private Long id;//id

    private Long userId;//用户id

    private Long blogId;//博客id

    private Long likesId;//点赞id

    private String userName;//用户名

    private String text;//文本

    private Integer share;//分享数

    private LocalDateTime createTime;//创建时间
}
