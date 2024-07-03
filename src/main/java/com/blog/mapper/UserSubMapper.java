package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Blog;
import com.blog.entity.UserSub;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSubMapper extends BaseMapper<UserSub> {
}
