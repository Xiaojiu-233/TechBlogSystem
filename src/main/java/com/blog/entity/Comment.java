package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.blog.entity.interfaces.MessageReact;
import lombok.Data;

import java.time.LocalDateTime;

//评论
@Data
public class Comment implements MessageReact<Comment> {
    private Long id;//id

    private Long userId;//用户id

    private Long blogId;//博客id

    private Long likesId;//点赞id

    private String userName;//用户名

    private String text;//文本

    private Integer share;//分享数

    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;//创建时间

    @Override
    public String objToMsg() {
        return "c:"+ userId + ":" + blogId + ":" + likesId + ":" + userName + ":" + text;
    }

    @Override
    public Comment msgToObj(String msg) {
        //数据处理与检查
        if(msg == null)return null;
        String[]ret = msg.split(":");
        if(ret.length < 6)return null;
        //数据装填
        this.id = IdWorker.getId();
        this.userId = ret[1].equals("null") ? null : Long.parseLong(ret[1]);
        this.blogId = ret[2].equals("null") ? null : Long.parseLong(ret[2]);
        this.likesId = ret[3].equals("null") ? null : Long.parseLong(ret[3]);
        this.userName = ret[4];
        this.text = ret[5];
        return this;
    }
}
