package com.blog.mapper.view;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Blog;
import com.blog.entity.view.LikesList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikesListMapper extends BaseMapper<LikesList> {
}
