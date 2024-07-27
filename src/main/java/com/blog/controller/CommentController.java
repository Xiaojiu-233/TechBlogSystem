package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dao.BlogDto;
import com.blog.dao.CommentDto;
import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.CommentService;
import com.blog.service.LikesService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.dao.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//评论的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/comment")
@Api(value = "评论的管理控制器",tags = "评论的管理控制器")
public class CommentController {

    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;
    @Resource
    private BlogService blogService;
    @Resource
    private LikesService likesService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate redisTemplate_2;

    //////////数据处理//////////

    //评论的分页查询(可以通过博客、用户id查询)
    @GetMapping("/page")
    @ApiOperation(value = "评论的分页查询", notes = "可以通过博客、用户id查询，管理员权限")
    public R<Page> page(int page, int pageSize, Long userId, Long blogId ){
        //权限判定
        if(!BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要管理员权限，你无权操作");
        log.info("正在进行分页查询 页数={} 页大小={} 查询博客id={} 查询博主id={}",page,pageSize,blogId,userId);

        //构造分页构造器
        Page<Comment> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(userId!= null,Comment::getUserId,userId);
        queryWrapper.eq(blogId!= null,Comment::getBlogId,blogId);
        //添加排序条件
        queryWrapper.orderByDesc(Comment::getCreateTime);
        //分页查询，结果返回给pageInfo
        commentService.page(pageInfo,queryWrapper);
        //返回结果
        return R.success(pageInfo);
    }

    //评论的本用户分页查询(可以通过博客id查询)
    @GetMapping("/userpage")
    @ApiOperation(value = "评论的本用户分页查询", notes = "查看自己的评论，可以通过博客id查询")
    public R<Page> userpage(int page, int pageSize, Long blogId){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在进行分页查询 页数={} 页大小={} 查询博客id={}",page,pageSize,blogId);
        //构造分页构造器
        Page<Comment> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(Comment::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(blogId!= null,Comment::getBlogId,blogId);
        //添加排序条件
        queryWrapper.orderByDesc(Comment::getCreateTime);
        //分页查询，结果返回给pageInfo
        commentService.page(pageInfo,queryWrapper);

        //进行dto装填处理
        Page<CommentDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<CommentDto> list = pageInfo.getRecords().stream().map((item)-> {
            CommentDto dto = new CommentDto();BeanUtils.copyProperties(item,dto);
            Integer[] ret = likesService.getLike(item.getLikesId(),BaseContext.getCurrentId());
            dto.setLikeNum(ret[1]);dto.setLikeState(ret[0]);
            return  dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        //返回结果
        return R.success(pageInfo);
    }

    //某博客的评论分页查询
    @GetMapping("/blogpage/{id}")
    @ApiOperation(value = "某博客的评论分页查询", notes = "某博客的评论分页查询")
    public R<Page> blogpage(int page, int pageSize,@PathVariable("id") Long blogId){
        //正式执行
        log.info("正在进行分页查询 页数={} 页大小={} 查询博客id={}",page,pageSize,blogId);
        //返回结果
        return R.success(commentService.listToDtoByBlog(page,pageSize,blogId));
    }

    //创建评论
    @PostMapping
    @ApiOperation(value = "创建评论", notes = "创建新的评论")
    public R<String> save(@RequestBody Comment comment){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行评论的创建: {}",comment);
        //判定用户与博客是否存在
        Blog blog = blogService.getById(comment.getBlogId());
        if(blog == null)return R.failure("评论创建失败，因为博客不存在");
        User user = userService.getById(BaseContext.getCurrentId());
        if(user == null)return R.failure("评论创建失败，因为用户不存在");
        //创建评论 与点赞信息
        comment.setId(IdWorker.getId());
        comment.setUserId(BaseContext.getCurrentId());
        comment.setUserName(user.getName());
        comment.setBlogId(comment.getBlogId());
        //发送给消息队列
        rabbitTemplate.convertAndSend("CommentCacheExchange","CommentCacheRouting",comment.objToMsg());
        //返回结果
        return R.success("评论发送成功") ;
    }

    //删除指定id的评论
    @PostMapping("/del/{ids}")
    @ApiOperation(value = "删除自己的评论", notes = "删除自己的评论，管理员也可以")
    public R<String> del(@PathVariable("ids") List<Long> ids){
        //正式执行
        log.info("正在执行评论的删除: {}",ids);
        //删除评论 与点赞信息
        boolean isAdmin = BaseContext.getIsAdmin();
        boolean success = commentService.DelCommAndRemoveLike(ids,isAdmin ? null : BaseContext.getCurrentId(),isAdmin);
        //返回结果
        return success ? R.success("评论删除成功") : R.failure("评论删除失败，可能原因为：存在非本用户的评论");
    }

    //转发指定id的评论
    @PostMapping("/share/{id}")
    @ApiOperation(value = "转发指定id的评论", notes = "后端用于记录")
    public R<String> del(@PathVariable("id") Long id){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行评论的转发记录: {}",id);
        //判定评论是否存在
        Comment comment = commentService.getById(id);
        if(comment == null)return R.failure("该id评论不存在");
        //转发评论
        comment.setShare(comment.getShare()+1);
        boolean success = commentService.updateById(comment);
        //修改数据后缓存方面 mysql先写redis再删
        redisTemplate_2.delete("share:c:" + comment.getId());
        //存至redis缓存，随机过期时间缓解缓存雪崩
        redisTemplate_2.opsForValue().set("share:c:" + comment.getId(),comment.getShare(),new Random().nextInt(100) + 200, TimeUnit.MINUTES);
        //返回结果
        return success ? R.success("评论转发成功") : R.failure("评论转发失败");
    }


}
