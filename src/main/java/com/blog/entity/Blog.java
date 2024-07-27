package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.blog.entity.interfaces.MessageReact;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

//博客
@Data
public class Blog implements MessageReact<Blog>  {
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

    @Override
    public String objToMsg() {
        return "b:"+ userId + ":" + title + ":" + text + ":" + images + ":" + likesId  + ":" + userName;
    }

    @Override
    public Blog msgToObj(String msg) {
        //数据处理与检查
        if(msg == null)return null;
        String[]ret = msg.split(":");
        if(ret.length < 7)return null;
        //数据装填
        this.id = IdWorker.getId();
        this.userId = ret[1].equals("null") ? null : Long.parseLong(ret[1]);
        this.title = ret[2];
        this.text = ret[3];
        this.images = ret[4];
        this.likesId = ret[5].equals("null") ? null : Long.parseLong(ret[5]);
        this.userName = ret[6];
        return this;
    }
}
