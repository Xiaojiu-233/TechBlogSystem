package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

//用户
@Data
public class User {
    private Long id;//id

    private String username;//用户账号

    private String password;//用户密码

    private String name;//用户昵称

    private String headImg;//头像图片链接

    private String phone;//电话号码

    private String sex;//性别

    private Integer age;//年龄

    private String sign;//个性签名

    private Long isLock;//是否封禁 0未封禁 此外的数字代表封禁时时间戳

    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime registerTime;//注册时间

    private LocalDateTime loginTime;//登录时间
}
