package com.blog.mapper.view;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.view.LikesList;
import com.blog.entity.view.LikesTarget;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikesTargetMapper extends BaseMapper<LikesTarget> {
}
