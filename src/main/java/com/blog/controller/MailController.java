package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Blog;
import com.blog.entity.Mail;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.MailService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//邮件的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/mail")
@Api(value = "邮件的管理控制器",tags = "邮件的管理控制器")
public class MailController {

    @Resource
    private MailService mailService;

    @Resource
    private UserService userService;

    //////////数据处理//////////

    //邮件的本用户分页查询(可以通过邮件标题模糊查询)
    @GetMapping("/userpage")
    @ApiOperation(value = "邮件的本用户分页查询", notes = "查看自己的邮件，可以通过邮件标题模糊查询")
    public R<Page> userpage(int page, int pageSize, String title){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() != null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在进行分页查询 页数={} 页大小={} 查询邮件名={}",page,pageSize,title);
        //构造分页构造器
        Page<Mail> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Mail> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（不需要展示text）
        queryWrapper.like(StringUtils.isNotBlank(title),Mail::getTitle,title);
        queryWrapper.eq(Mail::getUserId,BaseContext.getCurrentId());
        queryWrapper.select(Mail::getId,Mail::getCreateTime,Mail::getTitle,Mail::getFromName,Mail::getFromId,Mail::getIsRead,Mail::getUserId);
        //添加排序条件
        queryWrapper.orderByDesc(Mail::getCreateTime);
        //分页查询，结果返回给pageInfo
        mailService.page(pageInfo,queryWrapper);

        //返回结果
        return R.success(pageInfo);
    }

    //邮件的id查询
    @GetMapping("/{id}")
    @ApiOperation(value = "邮件的id查询", notes = "邮件的id查询，本用户操作，读完会把邮件设置为已读")
    public R<Mail> getById(@PathVariable("id")Long id){
        //正式执行
        log.info("正在执行邮件的按id查询：{}",id);
        //获取邮件
        Mail mail= mailService.getById(id);
        //排除异常情况
        if(mail==null)return R.failure("邮件查询失败");
        //邮件会设置为已读
        mail.setIsRead(1);
        mailService.updateById(mail);
        //返回结果
        return R.success(mail);
    }

    //发邮件
    @PostMapping
    @ApiOperation(value = "发邮件", notes = "发邮件")
    public R<String> save(@RequestBody Mail mail){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() != null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行邮件的发送: {}",mail);
        //判定用户是否存在
        User user = userService.getById(mail.getUserId());
        if(user == null)return R.failure("邮箱发送失败，因为目的用户不存在");
        user = userService.getById(BaseContext.getCurrentId());
        if(user == null)return R.failure("邮箱发送失败，因为来源用户不存在");
        //创建邮件
        mail.setId(IdWorker.getId());
        mail.setFromId(BaseContext.getCurrentId());
        mail.setFromName(user.getName());
        boolean success = mailService.save(mail);
        //返回结果
        return success ? R.success("邮件发送成功") : R.failure("邮件发送失败");
    }

    //删除指定id的邮件
    @PostMapping("/del/{ids}")
    @ApiOperation(value = "删除自己的邮件", notes = "删除自己的邮件")
    public R<String> del(@PathVariable("ids") List<Long> ids){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() != null)
            return R.failure("该操作需要用户来进行，你无权操作");
        Long uid = BaseContext.getCurrentId();
        //正式执行
        log.info("正在执行邮件的删除: {}",ids);
        //删除邮件 只要有一个删除成功就是成功
        LambdaQueryWrapper<Mail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Mail::getId,ids);
        queryWrapper.eq(uid != null,Mail::getUserId,uid);
        if(mailService.count(queryWrapper) == 0)return R.failure("邮件删除失败，不存在本用户的邮件");
        boolean success = mailService.remove(queryWrapper);
        //返回结果
        return success ? R.success("邮件删除成功") : R.failure("邮件删除失败");
    }

    //管理员群发邮件
    @PostMapping("/group")
    @ApiOperation(value = "管理员群发邮件", notes = "管理员权限")
    public R<String> group(@RequestBody Mail mail){
        //权限判定
        if(!BaseContext.getIsAdmin())
            return R.failure("该操作需要管理员权限，你无权操作");
        //正式执行
        log.info("正在执行邮件的群发送: {}",mail);
        //创建并发送群邮件

        boolean success = false;
        //返回结果
        return success ? R.success("邮件群发送成功") : R.failure("邮件群发送失败");
    }

    //读取邮件数
    @GetMapping("/count")
    @ApiOperation(value = "读取邮件数", notes = "用户权限")
    public R<Integer> count(){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() != null)
            return R.failure("该操作需要用户来进行，你无权操作");
        Long uid = BaseContext.getCurrentId();
        //正式执行
        log.info("正在执行邮件的数量读取: 用户id={}",uid);
        int count = -1;
        //读取邮件数
        LambdaQueryWrapper<Mail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Mail::getUserId,uid);
        count = mailService.count(queryWrapper);
        //返回结果
        return count > -1 ? R.success(count) : R.failure("邮件删除失败");
    }

}
