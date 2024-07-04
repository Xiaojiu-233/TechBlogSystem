package com.blog.mapper.view;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Blog;
import com.blog.entity.view.CollList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollListMapper extends BaseMapper<CollList> {
}
