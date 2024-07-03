package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.Likes;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.LikesMapper;
import com.blog.service.BlogService;
import com.blog.service.LikesService;
import com.blog.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes> implements LikesService {

//    @Resource(name = "redisTemplate_1")
//    private RedisTemplate redisTemplate_1;
//
//    @Resource(name = "redisTemplate_2")
//    private RedisTemplate redisTemplate_2;

    //读取点赞状态
    @Override
    public Integer[] getLike(Long likesId, Long userId) {
        Integer[] rets = new Integer[]{0,0};
//        //权限管理，只有用户才能执行此操作
//        if(BaseContext.getIsAdmin())
//            return rets;
//        //正式执行
//        log.info("正在执行获取点赞信息操作，点赞体id={},点赞者id={}",likesId,userId);
//        //用户点赞状态查询，查询redis缓存
//        ValueOperations op = redisTemplate_1.opsForValue();
//        ValueOperations op2 = redisTemplate_2.opsForValue();
//        rets[0] = (Integer) op.get(likesId.toString() + "::" + userId.toString());
//        if(rets[0] == null){
//            //没有缓存时读取mysql数据库
//            LambdaQueryWrapper<Likes> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(Likes::getLikesId,likesId);
//            queryWrapper.eq(Likes::getUserId,userId);
//            Likes like = getOne(queryWrapper);
//            rets[0] = like == null ? 0 : like.getStatus();
//        }
//        //点赞体的总点赞数
//        Integer count = (Integer) op2.get("likes" + likesId);
//        if(count != null){
//            rets[1] = count;return rets;
//        }else{
//            //读数据库
//            storeLike();
//            LambdaQueryWrapper<Likes> countWrapper = new LambdaQueryWrapper<>();
//            countWrapper.eq(Likes::getLikesId,likesId);
//            countWrapper.eq(Likes::getStatus,1);
//            rets[1] = count(countWrapper);
//            //写入点赞数访问缓存，保存2天
//            op2.set("likes" +  likesId,rets[1],2, TimeUnit.DAYS);
//        }

        //返回结果
        return rets;
    }

    //从redis缓存中拉取更新点赞数据
    @Override
    @Transactional
    public void storeLike() {
//        //读取redis的1号数据库所有key-value
//        Set<String> keys = redisTemplate_1.keys("*");
//        //如果没找到数据直接结束
//        if(keys.isEmpty())return;
//        //正式执行
//        ValueOperations operations = redisTemplate_1.opsForValue();
//        for (String key:keys){
//            //将读取到的数据处理后存入mysql数据库
//            String[] key_list = key.split("::");
//            Long likes_id = Long.parseLong(key_list[0]);
//            Long user_id = Long.parseLong(key_list[1]);
//            int value = (int) operations.get(key);
//            Likes like = new Likes(likes_id,user_id,value);
//            //添加或更新
//            LambdaQueryWrapper<Likes> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(Likes::getLikesId,likes_id);
//            queryWrapper.eq(Likes::getUserId,user_id);
//            if(getOne(queryWrapper) ==null)
//                save(like);
//            else update(like,queryWrapper);
//        }
//        //删除redis的1号数据库所有数据
//        redisTemplate_1.delete(keys);
    }
}
