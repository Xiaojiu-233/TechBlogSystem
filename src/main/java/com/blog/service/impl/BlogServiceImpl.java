package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.controller.LikesController;
import com.blog.entity.Blog;
import com.blog.entity.Likes;
import com.blog.entity.Mail;
import com.blog.entity.UserColl;
import com.blog.entity.view.LikesList;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.view.LikesListMapper;
import com.blog.service.BlogService;
import com.blog.service.CommentService;
import com.blog.service.LikesService;
import com.blog.service.UserCollService;
import com.blog.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Resource
    private LikesListMapper mapper;
    @Resource
    private LikesService likesService;
    @Resource
    private UserCollService userCollService;
    @Resource
    private CommentService commentService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    //添加博客并设置点赞数据
    @Override
    @Transactional
    public boolean CreateBlogAndSetLike(Blog blog) {
        //设置点赞id(需要在like_list检验是否重复)
        Long likeId = null;
        LambdaQueryWrapper<LikesList> queryWrapper = null;
        do {
            likeId = IdWorker.getId();
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LikesList::getLikesId, likeId);
        } while (mapper.selectCount(queryWrapper) != 0);
        blog.setLikesId(likeId);
        //返回结果
        return save(blog);
    }

    //删除博客并移除点赞数据(只要有一个删除成功就是成功)
    @Override
    @Transactional
    public boolean DelBlogAndRemoveLike(List<Long> ids, Long userId, boolean isAdmin) {
        //执行内容（一个一个删除，删除前留下标题以备管理员发邮箱）
        //构造条件构造器搜寻目标
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Blog::getId,ids);
        queryWrapper.eq(userId != null,Blog::getUserId,userId);
        if(count(queryWrapper) == 0)return false;
        //获取所有的博客然后一个一个删，先删除点赞，再删除博客
        List<Blog> list = list(queryWrapper);
        boolean success = false;
        for(Blog b : list){
            //删除收藏信息
            LambdaQueryWrapper<UserColl> collQueryWrapper = new LambdaQueryWrapper<>();
            collQueryWrapper.eq(UserColl::getBlogId, b.getId());
            userCollService.remove(collQueryWrapper);
            //删除点赞
            Long likeId = b.getLikesId();
            LambdaQueryWrapper<LikesList> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(LikesList::getLikesId, likeId);
            LambdaQueryWrapper<Likes> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Likes::getId, likeId);
            if(mapper.selectCount(queryWrapper1) > 0 && commentService.DelBlogCommAndRemoveLike(b.getId()) && likesService.remove(queryWrapper2)){
                //删除博客
                Long bid = b.getId();
                boolean deleted = getById(bid) != null && removeById(bid);
                if(deleted){
                    success = true;
                    //如果删除为管理员操作，需要通过邮件将信息告诉被 成功 删除博客的所有作者
                    if(isAdmin){
                        String msg = "您好，很抱歉地通知您：您的博客【" +b.getTitle() + "】由于不遵守社区规范，已被删除。";
                        rabbitTemplate.convertAndSend("MailCacheExchange","MailCacheRouting",
                                new Mail(null,b.getUserId(),null,"管理员","博客删除通知",msg,0,null).objToMsg());
                    }
                }
            }
        }
        return success;
    }
}
