package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.BlogService;
import com.blog.service.CommentService;

public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
