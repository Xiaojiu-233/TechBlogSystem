package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

//举报
@Data
public class Report {
    private Long id;//id

    private Long userId;//来源用户id

    private String text;//文本

    private Long targetId;//目标id

    private String target;//目标类型

    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;//创建时间
}
