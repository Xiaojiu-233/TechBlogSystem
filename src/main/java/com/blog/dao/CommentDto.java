package com.blog.dao;

import com.blog.entity.Blog;
import com.blog.entity.Comment;
import lombok.Data;

@Data
//评论的对外数据传输实体
public class CommentDto extends Comment {

    private Integer likeNum;//点赞数

    private Integer likeState;//点赞状态 0.没点赞 1.点赞了

}
