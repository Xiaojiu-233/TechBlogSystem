package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.blog.entity.interfaces.MessageReact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.message.Message;

import java.time.LocalDateTime;

//邮件
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mail implements MessageReact<Mail> {
    private Long id;//id

    private Long userId;//用户id

    private Long fromId;//来源用户id

    private String fromName;//来源用户名

    private String title;//标题

    private String text;//文本

    private Integer isRead;//是否阅读 1已读 0未读

    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;//创建时间

    @Override
    public String objToMsg() {
        return "m:"+  userId + ":" + fromId + ":" + fromName + ":" + title + ":" + text;
    }

    @Override
    public Mail msgToObj(String msg) {
        //数据处理与检查
        if(msg == null)return null;
        String[]ret = msg.split(":");
        if(ret.length < 6)return null;
        //数据装填
        this.id = IdWorker.getId();
        this.userId = ret[1].equals("null") ? null : Long.parseLong(ret[1]);
        this.fromId = ret[2].equals("null") ? null : Long.parseLong(ret[2]);
        this.fromName = ret[3];
        this.title = ret[4];
        this.text = ret[5];
        return this;
    }
}
