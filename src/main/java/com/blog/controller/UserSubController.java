package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.entity.UserColl;
import com.blog.entity.UserSub;
import com.blog.service.BlogService;
import com.blog.service.UserService;
import com.blog.service.UserSubService;
import com.blog.utils.BaseContext;
import com.blog.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//关注的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/userColl")
@Api(value = "关注的管理控制器",tags = "关注的管理控制器")
public class UserSubController {

    @Resource
    private UserSubService userSubService;

    @Resource
    private UserService userService;

    //////////业务处理//////////

    //关注&取消关注
    @PostMapping
    @ApiOperation(value = "关注&取消关注", notes = "用户权限")
    public R<String> sub(@RequestBody UserSub userSub){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行关注&取消关注操作: {}",userSub);
        //判定用户与博客是否存在
        userSub.setUserId(BaseContext.getCurrentId());
        User user = userService.getById(userSub.getUserId());
        if(user == null)return R.failure("关注处理失败，因为用户不存在");
        User subU = userService.getById(userSub.getSubId());
        if(subU == null)return R.failure("关注处理失败，因为关注的用户不存在");
        //判定是否有关注
        LambdaQueryWrapper<UserSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.equals(userSub);
        boolean isSub = userSubService.count(queryWrapper) > 0;
        //根据情况确定关注还是取消
        boolean success = isSub ? userSubService.remove(queryWrapper) : userSubService.save(userSub);
        //返回结果
        return success ? R.success(!isSub ? "关注":"取消关注" + "成功") : R.failure(!isSub ? "关注":"取消关注" + "失败");
    }

    //////////数据处理//////////

    //本用户关注情况分页查询
    @GetMapping("/userpage")
    @ApiOperation(value = "本用户关注情况分页查询", notes = "searchFans 0:否 1:是")
    public R<Page> userSub(int page, int pageSize, int userId,int searchFans){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在进行分页查询 页数={} 页大小={} 用户id={} 是否查询粉丝={}",page,pageSize,userId,searchFans);

        //读取所有的关注/粉丝用户
        LambdaQueryWrapper<UserSub> subLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(searchFans != 0)subLambdaQueryWrapper.eq(UserSub::getSubId,userId);
        else subLambdaQueryWrapper.eq(UserSub::getUserId,userId);
        List<UserSub> ret = userSubService.list(subLambdaQueryWrapper);
        List<Long> blogs = new ArrayList<>();
        for(UserSub us : ret){blogs.add(us.getSubId());}

        //构造分页构造器
        Page<User> pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.in(User::getId,blogs);
        //添加排序条件
        queryWrapper.orderByDesc(User::getRegisterTime);
        //只检索简要信息
        queryWrapper.select(User::getId,User::getName,User::getHeadImg,User::getSign,User::getSex);
        //分页查询，结果返回给pageInfo
        userService.page(pageInfo,queryWrapper);

        //返回结果
        return R.success(pageInfo);
    }

    //读取关注/粉丝数
    @GetMapping("/count")
    @ApiOperation(value = "读取某用户的关注/粉丝数", notes = "searchFans 0:否 1:是")
    public R<Integer> count(Long userId,int searchFans){
        //正式执行
        log.info("正在执行关注/粉丝的数量读取: 博客id={}",userId);
        int count = -1;
        //读取关注数
        LambdaQueryWrapper<UserSub> queryWrapper = new LambdaQueryWrapper<>();
        if(searchFans != 0)queryWrapper.eq(UserSub::getSubId,userId);
        else queryWrapper.eq(UserSub::getUserId,userId);
        count = userSubService.count(queryWrapper);
        //返回结果
        return count > -1 ? R.success(count) : R.failure("关注/粉丝数量读取失败");
    }

}
