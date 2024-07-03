package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.mapper.BlogMapper;
import com.blog.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    //添加博客并设置点赞数据
    @Override
    public boolean CreateBlogAndSetLike(Blog blog) {
        return false;
    }

    //删除博客并移除点赞数据(只要有一个删除成功就是成功)
    @Override
    public boolean DelBlogAndRemoveLike(List<Long> ids, Long userId, boolean isAdmin) {
        //执行内容（一个一个删除，删除前留下标题以备管理员发邮箱）
        //如果删除为管理员操作，需要通过邮件将信息告诉被 成功 删除博客的所有作者
        return false;
    }
}
