package com.blog.mapper.view;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Blog;
import com.blog.entity.view.UserSubs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSubsMapper extends BaseMapper<UserSubs> {
}
