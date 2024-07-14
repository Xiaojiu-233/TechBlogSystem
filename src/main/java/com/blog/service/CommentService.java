package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Blog;
import com.blog.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    boolean CreateCommAndSetLike(Comment comment);

    boolean DelCommAndRemoveLike(List<Long> ids, Long userId, boolean isAdmin);

    Page listToDtoByBlog(int page, int pageSize,  Long blogId);
}
