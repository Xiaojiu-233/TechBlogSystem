package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Mail;
import com.blog.entity.Report;
import com.blog.entity.User;
import com.blog.service.ReportService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.dao.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

//举报的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/report")
@Api(value = "举报的管理控制器",tags = "举报的管理控制器")
public class ReportController {

    @Resource
    private ReportService reportService;
    @Resource
    private UserService userService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    //////////数据处理//////////

    //举报的分页查询(管理员权限)
    @GetMapping("/page")
    @ApiOperation(value = "举报的分页查询", notes = "管理员权限")
    public R<Page> page(int page, int pageSize){
        //权限判定
        if(!BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要管理员来进行，你无权操作");
        //正式执行
        log.info("正在进行分页查询 页数={} 页大小={} ",page,pageSize);

        //构造分页构造器
        Page<Report> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByDesc(Report::getCreateTime);
        //分页查询，结果返回给pageInfo
        reportService.page(pageInfo,queryWrapper);

        //返回结果
        return R.success(pageInfo);
    }

    //处理指定id的举报（先处理完内容再调用它）
    @PostMapping("/handle/{id}")
    @ApiOperation(value = "处理指定id的举报（先处理完内容再调用它）", notes = "管理员权限")
    public R<String> handle(@PathVariable("id") Long id,String reason){
        //权限判定
        if(!BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要管理员来进行，你无权操作");
        //正式执行
        log.info("正在执行举报的处理: {}",id);
        //确定举报id是否存在
        Report report = reportService.getById(id);
        if(report == null) return  R.failure("该举报不存在");
        //处理举报
        boolean success = reportService.removeById(id);
        //将结果反馈给用户（通过邮箱通知，使用reason）
        if(success){
            String msg = "您好，您对" + report.getTarget() +"（id为" + report.getTargetId() + "）的举报已成功受理，感谢您为美化社区环境做出的贡献！。";
            rabbitTemplate.convertAndSend("MailCacheExchange","MailCacheRouting",
                    new Mail(null,report.getUserId(),null,"管理员","举报受理通知",msg,0,null).objToMsg());
        }
        //返回结果
        return success ? R.success("举报处理成功") : R.failure("举报处理失败");
    }

    //提交举报
    @PostMapping
    @ApiOperation(value = "提交举报", notes = "用户权限")
    public R<String> report(@RequestBody Report report){
        //权限判定
        if(BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要用户来进行，你无权操作");
        //正式执行
        log.info("正在执行举报的提交: {}",report);
        //判定用户是否存在
        User user = userService.getById(BaseContext.getCurrentId());
        if(user == null)return R.failure("举报提交失败，因为用户不存在");
        String type = report.getTarget();
        if(type == null ||  (!type.equals("用户") && !type.equals("评论")  && !type.equals("博客")))
            return R.failure("举报提交失败，因为举报类型并非用户、评论、博客的一种");
        //提交举报
        report.setId(IdWorker.getId());
        report.setUserId(BaseContext.getCurrentId());
        boolean success = reportService.save(report);
        //返回结果
        return success ? R.success("举报提交成功") : R.failure("举报提交失败");
    }




}
