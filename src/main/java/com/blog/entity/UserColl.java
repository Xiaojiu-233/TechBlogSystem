package com.blog.entity;

import lombok.Data;

//用户收藏博客
@Data
public class UserColl {

    private Long userId;//用户id

    private Long blogId;//博客id
}
