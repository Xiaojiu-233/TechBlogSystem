package com.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//用户关注
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSub {

    private Long userId;//用户id

    private Long subId;//关注用户id
}
