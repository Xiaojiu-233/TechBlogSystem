package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.entity.Likes;
import com.blog.entity.view.LikesList;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.view.LikesListMapper;
import com.blog.service.BlogService;
import com.blog.service.CommentService;
import com.blog.service.LikesService;
import com.blog.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private LikesListMapper mapper;

    @Resource
    private LikesService likesService;
    
    //添加评论并设置点赞数据
    @Override
    @Transactional
    public boolean CreateCommAndSetLike(Comment comment) {
        //设置点赞id(需要在like_list检验是否重复)
        Long likeId = null;
        LambdaQueryWrapper<LikesList> queryWrapper = null;
        do {
            likeId = IdWorker.getId();
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LikesList::getLikesId, likeId);
        } while (mapper.selectCount(queryWrapper) != 0);
        comment.setUserId(BaseContext.getCurrentId());
        comment.setLikesId(likeId);
        //返回结果
        return save(comment);
    }

    //删除评论并移除点赞数据(只要有一个删除成功就是成功)
    @Override
    @Transactional
    public boolean DelCommAndRemoveLike(List<Long> ids, Long userId, boolean isAdmin) {
        //执行内容（一个一个删除）
        //构造条件构造器搜寻目标
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Comment::getId,ids);
        queryWrapper.eq(userId != null,Comment::getUserId,userId);
        if(count(queryWrapper) == 0)return false;
        //获取所有的评论然后一个一个删，先删除点赞，再删除评论
        List<Comment> list = list(queryWrapper);
        boolean success = false;
        for(Comment c : list){
            //删除点赞
            Long likeId = c.getLikesId();
            LambdaQueryWrapper<LikesList> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(LikesList::getLikesId, likeId);
            LambdaQueryWrapper<Likes> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Likes::getId, likeId);
            if(mapper.selectCount(queryWrapper1) > 0 && likesService.remove(queryWrapper2)){
                //删除评论
                Long cid = c.getId();
                boolean deleted = getById(cid) != null && removeById(cid);
                if(deleted){
                    success = true;
                    //如果删除为管理员操作，需要通过邮件将信息告诉被 成功 删除评论的所有作者
                    if(isAdmin){
                        String text = c.getText();
                    }
                }
            }
        }
        return success;
    }
}
