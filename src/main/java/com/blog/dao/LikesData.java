package com.blog.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

//传输用点赞体
@Data
@AllArgsConstructor
public class LikesData {

    private Long target_id;//点赞体id

    private String likes_type;//点赞体样式，评论or博客

    private Integer likes_num;//点赞数
}
