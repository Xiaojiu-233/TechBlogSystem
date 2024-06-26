package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Blog;
import com.blog.mapper.BlogMapper;

import java.util.List;

public interface BlogService extends IService<Blog> {

    boolean CreateBlogAndSetLike(Blog blog);

    boolean DelBlogAndRemoveLike(List<Long> ids,Long userId,boolean isAdmin);
}
