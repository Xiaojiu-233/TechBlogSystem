package com.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

//用户阅读通知情况
@Data
@AllArgsConstructor
public class NoticeCheck {

    private Long userId;

    private Long checkMail;
}
