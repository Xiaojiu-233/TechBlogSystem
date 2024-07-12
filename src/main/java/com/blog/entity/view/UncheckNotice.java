package com.blog.entity.view;

import lombok.Data;

//用户未读通知数
@Data
public class UncheckNotice {

    private Long userId;

    private Integer num;
}
