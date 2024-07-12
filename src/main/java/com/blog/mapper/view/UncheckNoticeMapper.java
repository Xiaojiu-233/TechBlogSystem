package com.blog.mapper.view;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.view.LikesTarget;
import com.blog.entity.view.UncheckNotice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UncheckNoticeMapper extends BaseMapper<UncheckNotice> {
}
