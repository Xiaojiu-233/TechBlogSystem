package com.blog.entity.view;

import lombok.Data;

//点赞目标用户
@Data
public class LikesTarget {

    private Long likesId;//点赞id

    private Long blogId;//点赞体id 博客

    private Long commentId;//点赞体id 评论

    private Long id;//点赞目标用户id
}
