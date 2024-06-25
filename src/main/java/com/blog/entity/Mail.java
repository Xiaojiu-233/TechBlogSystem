package com.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

//邮件
@Data
public class Mail {
    private Long id;//id

    private Long userId;//用户id

    private Long fromId;//来源用户id

    private String fromName;//来源用户名

    private String title;//标题

    private String text;//文本

    private Integer isRead;//是否阅读 1已读 0未读

    private LocalDateTime createTime;//创建时间
}
