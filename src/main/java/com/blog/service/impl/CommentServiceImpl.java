package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.BlogService;
import com.blog.service.CommentService;

import java.util.List;

public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    //添加评论并设置点赞数据
    @Override
    public boolean CreateCommAndSetLike(Comment comment) {
        return false;
    }

    //删除评论并移除点赞数据(只要有一个删除成功就是成功)
    @Override
    public boolean DelCommAndRemoveLike(List<Long> ids, Long userId, boolean isAdmin) {
        //执行内容（一个一个删除，删除前留下标题以备管理员发邮箱）
        //如果删除为管理员操作，需要通过邮件将信息告诉被 成功 删除评论的所有作者
        return false;
    }
}
