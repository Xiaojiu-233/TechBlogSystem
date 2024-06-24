package com.blog.entity;

import lombok.Data;

//用户关注
@Data
public class UserSub {

    private Long userId;//用户id

    private Long subId;//关注用户id
}
