package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.entity.UserColl;
import com.blog.entity.view.CollList;
import com.blog.mapper.view.CollListMapper;
import com.blog.service.BlogService;
import com.blog.service.UserCollService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.dao.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//收藏的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/userColl")
@Api(value = "收藏的管理控制器",tags = "收藏的管理控制器")
public class UserCollController {

    @Resource
    private UserCollService userCollService;

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @Resource
    private CollListMapper collListMapper;

    //////////业务处理//////////

    //收藏&取消收藏
    @PostMapping
    @ApiOperation(value = "收藏&取消收藏", notes = "用户权限")
    public synchronized R<String> coll(@RequestBody UserColl userColl){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行收藏&取消收藏操作: {}",userColl);
        //判定用户与博客是否存在
        userColl.setUserId(BaseContext.getCurrentId());
        User user = userService.getById(userColl.getUserId());
        if(user == null)return R.failure("收藏处理失败，因为用户不存在");
        Blog blog = blogService.getById(userColl.getBlogId());
        if(blog == null)return R.failure("收藏处理失败，因为博客不存在");
        //判定是否有收藏
        LambdaQueryWrapper<UserColl> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.equals(userColl);
        boolean isColl = userCollService.count(queryWrapper) > 0;
        //根据情况确定收藏还是取消
        boolean success = isColl ? userCollService.remove(queryWrapper) : userCollService.save(userColl);
        //返回结果
        return success ? R.success(!isColl ? "收藏":"取消收藏" + "成功") : R.failure(!isColl ? "收藏":"取消收藏" + "失败");
    }

    //////////数据处理//////////

    //收藏的本用户分页查询(可以通过收藏标题模糊查询)
    @GetMapping("/userpage")
    @ApiOperation(value = "收藏的本用户分页查询", notes = "查看自己的收藏，可以通过收藏标题模糊查询")
    public R<Page> userpage(int page, int pageSize){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在进行分页查询 页数={} 页大小={}",page,pageSize);

        //读取所有的收藏博客
        LambdaQueryWrapper<UserColl> collLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collLambdaQueryWrapper.eq(UserColl::getUserId,BaseContext.getCurrentId());
        List<UserColl> ret = userCollService.list(collLambdaQueryWrapper);
        List<Long> blogs = new ArrayList<>();
        for(UserColl uc : ret){blogs.add(uc.getBlogId());}

        //构造分页构造器
        Page<Blog> pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.in(Blog::getId,blogs);
        //添加排序条件
        queryWrapper.orderByDesc(Blog::getCreateTime);
        //不用检索text
        queryWrapper.select(Blog::getId,Blog::getCreateTime,Blog::getTitle,Blog::getLikesId,Blog::getImages,Blog::getShare,Blog::getUserId,Blog::getUserName);
        //分页查询，结果返回给pageInfo
        blogService.page(pageInfo,queryWrapper);

        //返回结果
        return R.success(pageInfo);
    }

    //读取收藏数
    @GetMapping("/count")
    @ApiOperation(value = "读取某博客的收藏数", notes = "读取某博客的收藏数")
    public R<Integer> count(Long blogId){
        //正式执行
        log.info("正在执行收藏的数量读取: 博客id={}",blogId);
        int count = -1;
        //读取收藏数
        LambdaQueryWrapper<CollList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollList::getBlogId,blogId);
        count = collListMapper.selectCount(queryWrapper);
        //返回结果
        return count > -1 ? R.success(count) : R.failure("收藏数量读取失败");
    }

}
