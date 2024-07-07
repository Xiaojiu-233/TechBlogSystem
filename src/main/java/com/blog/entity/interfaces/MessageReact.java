package com.blog.entity.interfaces;

//用于实现实体对象与消息队列消息的数据交互
public interface MessageReact<T> {

    //实体对象变为message
    String objToMsg();

    //message变为实体对象(数据装入自身)
    T msgToObj(String msg);

}
