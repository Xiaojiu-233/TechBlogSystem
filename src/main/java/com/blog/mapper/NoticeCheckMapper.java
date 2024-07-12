package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Mail;
import com.blog.entity.NoticeCheck;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeCheckMapper extends BaseMapper<NoticeCheck> {
}
