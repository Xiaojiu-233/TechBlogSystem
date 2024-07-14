package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.config.RedisConfig;
import com.blog.entity.Blog;
import com.blog.entity.Likes;
import com.blog.entity.view.LikesList;
import com.blog.entity.view.LikesTarget;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.LikesMapper;
import com.blog.mapper.view.LikesListMapper;
import com.blog.mapper.view.LikesTargetMapper;
import com.blog.service.BlogService;
import com.blog.service.LikesService;
import com.blog.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes> implements LikesService {

    @Resource(name = "redisTemplate_1")
    private RedisTemplate redisTemplate_1;
    @Resource(name = "redisTemplate_2")
    private RedisTemplate redisTemplate_2;
    @Resource(name = "redisTemplate_4")
    private RedisTemplate redisTemplate_4;

    @Resource
    private LikesListMapper likesListMapper;
    @Resource
    private LikesTargetMapper likesTargetMapper;

    //读取点赞状态
    @Override
    public Integer[] getLike(Long likesId, Long userId) {
        Integer[] rets = new Integer[]{0,0};//第一个是点赞状态，第二个是点赞总数
        //权限管理，只有用户才能执行此操作
        if(BaseContext.getIsAdmin())
            return rets;
        //正式执行
        log.info("正在执行获取点赞信息操作，点赞体id={},点赞者id={}",likesId,userId);
        //用户点赞状态查询，查询redis缓存
        HashOperations op = redisTemplate_1.opsForHash();
        rets[0] = (Integer) op.get(likesId.toString(),userId.toString());
        if(rets[0] == null){
            //没有缓存时读取mysql数据库
            LambdaQueryWrapper<Likes> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Likes::getId,likesId);
            queryWrapper.eq(Likes::getUserId,userId);
            Likes like = getOne(queryWrapper);
            rets[0] = like == null ? 0 : like.getStates();
        }
        //点赞体的总点赞数
        ValueOperations op2 = redisTemplate_2.opsForValue();
        Integer count = (Integer) op2.get("likes" + likesId);
        if(count == null){
            //使用互斥锁解决缓存击穿和缓存穿透问题
            while(true) {
                if (RedisConfig.reenLock.tryLock()) {
                    //读数据库
                    storeLike();
                    LambdaQueryWrapper<LikesList> countWrapper = new LambdaQueryWrapper<>();
                    rets[1] = likesListMapper.selectCount(countWrapper);
                    //写入点赞数访问缓存，保存2天
                    op2.set("likes" + likesId, rets[1], 2, TimeUnit.DAYS);
                    //解放锁，结束循环
                    RedisConfig.reenLock.unlock();
                    break;
                } else {
                    //取锁失败则查看缓存，如果依然没有就等一会儿再尝试取锁
                    count = (Integer) op2.get("likes" + likesId);
                    if(count != null){try {Thread.sleep(100);}
                    catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else break;
                }
            }
        }
        rets[1] = count;
        //返回结果
        return rets;
    }

    //从redis缓存中拉取更新点赞数据
    @Override
    @Transactional
    public void storeLike() {
        //读取redis的1号数据库所有key-value
        Set<String> keys = redisTemplate_1.keys("*");
        //如果没找到数据直接结束
        if(keys.isEmpty())return;
        //正式执行
        LambdaQueryWrapper<LikesList> likeQueryWrapper;
        HashOperations operations = redisTemplate_1.opsForHash();
        for (String key:keys){
            Map<String,Integer> entries = operations.entries(key);
            for(String uid:entries.keySet()){
                //将读取到的数据处理后存入mysql数据库
                Long likes_id = Long.parseLong(key);
                //查询likesList有没有这个likes_id的数据，没有就算了
                likeQueryWrapper = new LambdaQueryWrapper<>();
                likeQueryWrapper.eq(LikesList::getLikesId,likes_id);
                if(likesListMapper.selectCount(likeQueryWrapper) <= 0)continue;
                //如果存在这个likes_id，就继续执行
                Long user_id = Long.parseLong(uid);
                int value = entries.get(uid);
                Likes like = new Likes(likes_id,user_id,value);
                //存储到redis4号数据库作为点赞通知缓存(自己给自己的点赞通知就不需要搞了)
                LambdaQueryWrapper<LikesTarget> noticeQueryWrapper = new LambdaQueryWrapper<>();
                noticeQueryWrapper.eq(LikesTarget::getLikesId,likes_id);
                LikesTarget target = likesTargetMapper.selectOne(noticeQueryWrapper);
                if(target != null && value == 1 && !Objects.equals(target.getId(), user_id)){
                    SetOperations setOperations = redisTemplate_4.opsForSet();
                    setOperations.add(target.getId().toString(),(target.getBlogId() == null ?
                            "评论:" + target.getCommentId() :"博客:" + target.getBlogId() ) + "#" + likes_id);
                }
                //添加或更新
                LambdaQueryWrapper<Likes> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Likes::getId,likes_id);
                queryWrapper.eq(Likes::getUserId,user_id);
                if(getOne(queryWrapper) ==null)
                    save(like);
                else update(like,queryWrapper);
            }
        }
        //删除redis的1号数据库所有数据
        redisTemplate_1.delete(keys);
    }
}
