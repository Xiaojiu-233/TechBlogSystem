package com.blog.controller;

import com.blog.dao.LikesData;
import com.blog.service.LikesService;
import com.blog.utils.BaseContext;
import com.blog.dao.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

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
    @Resource(name = "redisTemplate_4")
    private RedisTemplate redisTemplate_4;

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

    //获取自己被点赞数
    @GetMapping("/count")
    @ApiOperation(value = "获取自己被点赞数", notes = "用户权限")
    public R<Integer> count(){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        Long userId = BaseContext.getCurrentId();
        //读取数据
        int count = 0;
        SetOperations operations = redisTemplate_4.opsForSet();
        Long c = operations.size(userId.toString());
        if(c != null)count = Math.toIntExact(c);
        //返回结果
        return R.success(count);
    }

    //获取自己的被点赞通知，之后删除点赞通知
    @GetMapping("/notice")
    @ApiOperation(value = "获取自己被点赞通知", notes = "用户权限，得到结果的目标id都是博客的")
    public R<List<LikesData>> notice(){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        Long userId = BaseContext.getCurrentId();
        //读取数据
        List<LikesData> ret = new ArrayList<>();
        SetOperations operations = redisTemplate_4.opsForSet();
        Set<String> msg = operations.members(userId.toString());
        //处理数据
        Map<String,Integer> countMap = new HashMap<>();
        for(String m : msg){
            String key = m.split("#")[0];
            countMap.put(key,countMap.getOrDefault(key,0)+1);
        }
        for(String k : countMap.keySet()){
            String[] ksp = k.split(":");
            ret.add(new LikesData(Long.parseLong(ksp[1]),ksp[0],countMap.get(k)));
        }
        //删除缓存数据
        redisTemplate_4.delete(userId.toString());
        //返回结果
        return R.success(ret);
    }

}
