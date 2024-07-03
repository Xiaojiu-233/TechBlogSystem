package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

//评论
@Data
public class Comment {
    private Long id;//id

    private Long userId;//用户id

    private Long blogId;//博客id

    private Long likesId;//点赞id

    private String userName;//用户名

    private String text;//文本

    private Integer share;//分享数

    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;//创建时间
}
