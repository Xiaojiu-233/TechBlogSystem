package com.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

//管理员
@Data
public class Emp {
    private Long id;//id

    private String username;//管理员账号

    private String password;//管理员密码

    private LocalDateTime createTime;//创建时间

    private LocalDateTime loginTime;//登录时间
}
