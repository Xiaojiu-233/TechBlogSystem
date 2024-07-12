package com.blog.dao;

import lombok.Data;

import java.io.Serializable;

//数据传输实体
@Data
public class R<T> implements Serializable {

    private int code; //1为成功，0和其他代表失败

    private String msg;//错误信息

    private T data;//数据

    public static <T> R<T> success(T data){
        R<T> r = new R<>();
        r.code = 1;
        r.data= data;
        return  r;
    }

    public static <T> R<T> failure(String msg){
        R<T> r = new R<>();
        r.code = 0;
        r.msg= msg;
        return  r;
    }
}
