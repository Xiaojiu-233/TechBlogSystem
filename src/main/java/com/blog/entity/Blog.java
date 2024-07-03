package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

//博客
@Data
public class Blog {
    private Long id;//id

    private Long userId;//用户id

    private String title;//标题

    private String text;//文本

    private String images;//图片链接

    private Long likesId;//点赞id

    private String userName;//用户名

    private Integer share;//分享数

    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;//创建时间
}
