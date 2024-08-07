package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.config.RedisConfig;
import com.blog.dao.BlogDto;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.LikesService;
import com.blog.service.UserCollService;
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

//博客的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/blog")
@Api(value = "博客的管理控制器",tags = "博客的管理控制器")
public class BlogController {

    @Resource
    private BlogService blogService;
    @Resource
    private UserService userService;
    @Resource
    private LikesService likesService;
    @Resource
    private UserCollService userCollService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate redisTemplate_2;

    //////////数据处理//////////

    //博客的分页查询(可以通过博客标题模糊查询)
    @GetMapping("/page")
    @ApiOperation(value = "博客的分页查询", notes = "可以通过博客标题等信息查询")
    public R<Page> page(int page, int pageSize, String title,Long userId ){
        log.info("正在进行分页查询 页数={} 页大小={} 查询博客名={} 查询博主id={}",page,pageSize,title,userId);

        //构造分页构造器
        Page<Blog> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(title),Blog::getTitle,title);
        queryWrapper.eq(userId != null,Blog::getUserId,userId);
        //添加排序条件
        queryWrapper.orderByDesc(Blog::getCreateTime);
        //不用检索text
        queryWrapper.select(Blog::getId,Blog::getCreateTime,Blog::getTitle,Blog::getLikesId,Blog::getImages,Blog::getShare,Blog::getUserId,Blog::getUserName);
        //分页查询，结果返回给pageInfo
        blogService.page(pageInfo,queryWrapper);

        //进行dto装填处理
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null) return R.success(pageInfo);
        Page<BlogDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<BlogDto> list = pageInfo.getRecords().stream().map((item)-> {
            BlogDto dto = new BlogDto();BeanUtils.copyProperties(item,dto);
            Integer[] ret = likesService.getLike(item.getLikesId(),BaseContext.getCurrentId());
            dto.setLikeNum(ret[1]);dto.setLikeState(ret[0]);
            return  dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        //返回结果
        return R.success(dtoPage);
    }

    //博客的本用户分页查询(可以通过博客标题模糊查询)
    @GetMapping("/userpage")
    @ApiOperation(value = "博客的本用户分页查询", notes = "查看自己的博客，可以通过博客标题模糊查询")
    public R<Page> userpage(int page, int pageSize, String title){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在进行分页查询 页数={} 页大小={} 查询博客名={}",page,pageSize,title);
        //构造分页构造器
        Page<Blog> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(title),Blog::getTitle,title);
        queryWrapper.eq(Blog::getUserId,BaseContext.getCurrentId());
        //添加排序条件
        queryWrapper.orderByDesc(Blog::getCreateTime);
        //不用检索text
        queryWrapper.select(Blog::getId,Blog::getCreateTime,Blog::getTitle,Blog::getLikesId,Blog::getImages,Blog::getShare,Blog::getUserId,Blog::getUserName);
        //分页查询，结果返回给pageInfo
        blogService.page(pageInfo,queryWrapper);

        //进行dto装填处理
        Page<BlogDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<BlogDto> list = pageInfo.getRecords().stream().map((item)-> {
            BlogDto dto = new BlogDto();BeanUtils.copyProperties(item,dto);
            Integer[] ret = likesService.getLike(item.getLikesId(),BaseContext.getCurrentId());
            dto.setLikeNum(ret[1]);dto.setLikeState(ret[0]);
            return  dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        //返回结果
        return R.success(pageInfo);
    }

    //博客的id查询
    @GetMapping("/{id}")
    @ApiOperation(value = "博客的id查询", notes = "博客的id查询")
    public R<Blog> getById(@PathVariable("id")Long id){
        //正式执行
        log.info("正在执行博客的按id查询：{}",id);

        //博客数据
        Blog blog= null;
        //如果有redis缓存的话使用redis缓存，没有的话再查数据库
        blog = (Blog) redisTemplate_2.opsForValue().get("blog:" + id);
        if(blog == null){
            //使用互斥锁解决缓存击穿和缓存穿透问题
            while(true){
                if(RedisConfig.reenLock.tryLock()){
                    //获取博客
                    blog= blogService.getById(id);
                    //排除异常情况
                    if(blog==null)return R.failure("博客查询失败");
                    //存至redis缓存，随机过期时间缓解缓存雪崩
                    redisTemplate_2.opsForValue().set("blog:" + id,blog,new Random().nextInt(100) + 200, TimeUnit.MINUTES);
                    //解放锁，结束循环
                    RedisConfig.reenLock.unlock();break;
                }else{
                    //取锁失败则查看缓存，如果依然没有就等一会儿再尝试取锁
                    blog = (Blog) redisTemplate_2.opsForValue().get("blog:" + id);
                    if(blog == null){try {Thread.sleep(100);}
                    catch (InterruptedException e) {throw new RuntimeException(e);}}
                    else break;
                }
            }
        }

        //转发数据
        //如果有redis缓存的话使用redis缓存，没有的话再查数据库
        Integer shareCount = (Integer) redisTemplate_2.opsForValue().get("share:b:" + id);
        if(shareCount == null){
            //使用互斥锁解决缓存击穿和缓存穿透问题
            while(true){
                if(RedisConfig.reenLock.tryLock()){
                    shareCount = blog.getShare();
                    //存至redis缓存，随机过期时间缓解缓存雪崩
                    redisTemplate_2.opsForValue().set("share:b:" + id,shareCount,new Random().nextInt(100) + 200, TimeUnit.MINUTES);
                    //解放锁，结束循环
                    RedisConfig.reenLock.unlock();break;
                }else{
                    //取锁失败则查看缓存，如果依然没有就等一会儿再尝试取锁
                    shareCount = (Integer) redisTemplate_2.opsForValue().get("share:b:" + id);
                    if(shareCount == null){try {Thread.sleep(100);}
                    catch (InterruptedException e) {throw new RuntimeException(e);}}
                    else break;
                }
            }}
        blog.setShare(shareCount);
        //返回结果
        return R.success(blog);
    }

    //创建博客
    @PostMapping("/{time}")
    @ApiOperation(value = "创建博客", notes = "创建新的博客，参数targetDate存放延时发布的时间戳")
    public R<String> save(@RequestBody Blog blog,@PathVariable("time") Long targetDate){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行博客的创建: {}",blog);
        //判定用户是否存在
        User user = userService.getById(BaseContext.getCurrentId());
        if(user == null)return R.failure("博客创建失败，因为用户不存在");
        //创建博客 与点赞信息
        blog.setId(IdWorker.getId());
        blog.setUserId(BaseContext.getCurrentId());
        blog.setUserName(user.getName());
        //发送给消息队列
        Long delayTime;
        if(targetDate > 0) delayTime = targetDate - System.currentTimeMillis();
        else delayTime = 1L;
        //确定时间
        if(delayTime < 0) return R.failure("延迟时间太短了或者在当今时间之前，请选择比当前日期迟2-3分钟的的时间");
        //执行
        rabbitTemplate.convertAndSend("BlogPublishExchange", "BlogPublishRouting", blog.objToMsg(), message -> {
            message.getMessageProperties().setExpiration(String.valueOf(delayTime));
            return message;
        });
        //返回结果
        return R.success("博客创建成功") ;
    }

    //修改博客
    @PostMapping("/upd")
    @ApiOperation(value = "修改博客", notes = "修改自己的博客")
    public R<String> upd(@RequestBody Blog blog){
        //权限判定
        if(BaseContext.getIsAdmin() || !BaseContext.getCurrentId().equals(blog.getUserId()))
            return R.failure("该操作需要自己的用户来进行，你无权操作");
        //正式执行
        log.info("正在执行博客的修改: {}",blog);
        //修改（需要防止转发数篡改！）
        Blog oldBlog = blogService.getById(blog.getId());
        blog.setShare(oldBlog.getShare());
        boolean success = blogService.updateById(blog);
        //修改数据后缓存方面 mysql先写redis再删
        redisTemplate_2.delete("blog:" + blog.getId());
        //返回结果
        return success ? R.success("博客修改成功") : R.failure("博客修改失败");
    }

    //删除指定id的博客
    @PostMapping("/del/{ids}")
    @ApiOperation(value = "删除自己的博客", notes = "删除自己的博客，管理员也可以")
    public R<String> del(@PathVariable("ids") List<Long> ids){
        //正式执行
        log.info("正在执行博客的删除: {}",ids);
        //删除博客 与点赞信息
        boolean isAdmin = BaseContext.getIsAdmin();
        boolean success = blogService.DelBlogAndRemoveLike(ids,isAdmin ? null : BaseContext.getCurrentId(),isAdmin);
        //修改数据后缓存方面 mysql先写redis再删
        for(Long id : ids) redisTemplate_2.delete("blog:" + id);
        //返回结果
        return success ? R.success("博客删除成功") : R.failure("博客删除失败，可能原因为：存在非本用户的博客");
    }

    //转发指定id的博客
    @PostMapping("/share/{id}")
    @ApiOperation(value = "转发指定id的博客", notes = "后端用于记录")
    public synchronized R<String> share(@PathVariable("id") Long id){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行博客的转发记录: {}",id);
        //判定博客是否存在
        Blog blog = blogService.getById(id);
        if(blog == null)return R.failure("该id博客不存在");
        //转发博客
        blog.setShare(blog.getShare()+1);
        boolean success = blogService.updateById(blog);
        //修改数据后缓存方面 mysql先写redis再删
        redisTemplate_2.delete("share:b:" + blog.getId());
        //存至redis缓存，随机过期时间缓解缓存雪崩
        redisTemplate_2.opsForValue().set("share:b:" + blog.getId(),blog.getShare(),new Random().nextInt(100) + 200, TimeUnit.MINUTES);
        //返回结果
        return success ? R.success("博客转发成功") : R.failure("博客转发失败");
    }


}
