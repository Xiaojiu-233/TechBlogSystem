package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.LikesService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//点赞的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/like")
@Api(value = "点赞的管理控制器",tags = "点赞的管理控制器")
public class LikesController {

    @Resource(name = "redisTemplate_1")
    private RedisTemplate redisTemplate_1;

    @Resource(name = "redisTemplate_2")
    private RedisTemplate redisTemplate_2;

    @Resource
    private LikesService likesService;

    //////////业务处理//////////
    //点赞&取消点赞
    @PostMapping
    @ApiOperation(value = "点赞&取消点赞", notes = "创建新的点赞")
    public synchronized R<String> save(Long likesId,Integer status){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        Long userId = BaseContext.getCurrentId();
        //将设置点赞结果存储到redis数据库中作为缓存
        log.info("正在执行设置点赞信息操作，点赞体id={},点赞者id={},点赞状态={}",likesId,userId,status);
        HashOperations operations = redisTemplate_1.opsForHash();
        operations.put(likesId.toString(),userId.toString(),status);
        //删除点赞体缓存
        redisTemplate_2.delete("likes" +  likesId);
        //返回结果
        return R.success((status == 1 ? "点赞" : "取消点赞") + "操作成功！");
    }

    //////////数据处理//////////

    //将redis缓存的数据存入(后端自调用)
    @Transactional
    public R<String> storeData(){
        //执行
        likesService.storeLike();
        return R.success("数据转移成功！");
    }


}
