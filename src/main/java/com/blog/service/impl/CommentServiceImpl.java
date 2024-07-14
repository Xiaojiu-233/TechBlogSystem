package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.config.RedisConfig;
import com.blog.dao.CommentDto;
import com.blog.entity.Comment;
import com.blog.entity.Likes;
import com.blog.entity.Mail;
import com.blog.entity.view.LikesList;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.view.LikesListMapper;
import com.blog.service.CommentService;
import com.blog.service.LikesService;
import com.blog.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private LikesListMapper mapper;
    @Resource
    private LikesService likesService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate redisTemplate_2;
    
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
        //comment相关缓存 指向的是comment所在的blog的id 修改数据后缓存方面 mysql先写redis再删
        redisTemplate_2.delete("comment:" + comment.getBlogId());
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
                        text = text.length() > 10 ? text.substring(0,10) + "..." : text;
                        //如果删除为管理员操作，需要通过邮件将信息告诉被 成功 评论博客的所有作者
                        if(isAdmin){
                            String msg = "您好，很抱歉地通知您：您的评论【" + text + "】由于不遵守社区规范，已被删除。";
                            rabbitTemplate.convertAndSend("MailCacheExchange","MailCacheRouting",
                                    new Mail(null,c.getUserId(),null,"管理员","评论删除通知",msg,0,null).objToMsg());
                        }
                    }
                }
            }
            //comment相关缓存 指向的是comment所在的blog的id 修改数据后缓存方面 mysql先写redis再删
            for(Long id : ids) redisTemplate_2.delete("comment:" + c.getBlogId());
        }
        return success;
    }

    //通过blog的id将数据转移为dto
    @Override
    public Page listToDtoByBlog(int page, int pageSize, Long blogId) {
        Page<CommentDto> dtoPage = new Page(page,pageSize);
        List<Comment> dataList = null;
        List<CommentDto> dtoList = new ArrayList<>();
        //如果有redis缓存的话使用redis缓存，没有的话再查数据库
        dataList = (List<Comment>) redisTemplate_2.opsForValue().get("comment:" + blogId);
        if(dataList == null){
            //使用互斥锁解决缓存击穿和缓存穿透问题
            while(true){
                if(RedisConfig.reenLock.tryLock()){
                    //构造条件构造器
                    LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
                    //添加过滤条件
                    queryWrapper.eq(Comment::getBlogId,blogId);
                    //添加排序条件
                    queryWrapper.orderByDesc(Comment::getCreateTime);
                    //搜索
                    dataList = list(queryWrapper);
                    //存至redis缓存，随机过期时间缓解缓存雪崩
                    redisTemplate_2.opsForValue().set("comment:" + blogId,dataList,new Random().nextInt(100) + 200, TimeUnit.MINUTES);
                    //解放锁，结束循环
                    RedisConfig.reenLock.unlock();break;
                }else{
                    //取锁失败则查看缓存，如果依然没有就等一会儿再尝试取锁
                    dataList = (List<Comment>) redisTemplate_2.opsForValue().get("comment:" + blogId);
                    if(dataList == null){try {Thread.sleep(100);}
                    catch (InterruptedException e) {throw new RuntimeException(e);}}
                    else break;
                }
            }

        }
        //进行dto装填处理
        int index = (page - 1) * pageSize;
        for(int i = 0; i < pageSize && index < dataList.size();i++,index++){
            Comment item = dataList.get(index);
            //检索正确或没有检索时
            CommentDto dto = new CommentDto();
            BeanUtils.copyProperties(item,dto);
            //读取点赞数据
            if(!BaseContext.getIsAdmin() && BaseContext.getCurrentId() != null) {
                Integer[] ret = likesService.getLike(item.getLikesId(),BaseContext.getCurrentId());
                dto.setLikeNum(ret[1]);dto.setLikeState(ret[0]);
            }
            //读取转发数据
            //如果有redis缓存的话使用redis缓存，没有的话再查数据库
            Integer shareCount = (Integer) redisTemplate_2.opsForValue().get("share:c:" + item.getId());
            if(shareCount == null){
                //使用互斥锁解决缓存击穿和缓存穿透问题
                while(true){
                    if(RedisConfig.reenLock.tryLock()){
                        shareCount = item.getShare();
                        //存至redis缓存，随机过期时间缓解缓存雪崩
                        redisTemplate_2.opsForValue().set("share:c:" + item.getId(),shareCount,new Random().nextInt(100) + 200, TimeUnit.MINUTES);
                        //解放锁，结束循环
                        RedisConfig.reenLock.unlock();break;
                    }else{
                        //取锁失败则查看缓存，如果依然没有就等一会儿再尝试取锁
                        shareCount = (Integer) redisTemplate_2.opsForValue().get("share:c:" + item.getId());
                        if(shareCount == null){try {Thread.sleep(100);}
                        catch (InterruptedException e) {throw new RuntimeException(e);}}
                        else break;
                    }
                }
            }
            dto.setShare(shareCount);
            //添加数据
            dtoList.add(dto);
        }
        dtoPage.setRecords(dtoList);
        dtoPage.setTotal(dataList.size());
        //返回结果
        return dtoPage;
    }
}
