package com.blog.component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.blog.utils.BaseContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //插入时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("公共字段自动填充，插入时的元数据： " + metaObject);
        if(metaObject.hasSetter("createTime")) metaObject.setValue("createTime", LocalDateTime.now());
        if(metaObject.hasSetter("registerTime")) metaObject.setValue("registerTime", LocalDateTime.now());

    }

    //更新时自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
    }
}