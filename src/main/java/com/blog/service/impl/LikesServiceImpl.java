package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.Likes;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.LikesMapper;
import com.blog.service.BlogService;
import com.blog.service.LikesService;

public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes> implements LikesService {
}
