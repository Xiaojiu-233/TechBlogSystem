package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.component.RabbitmqLogManager;
import com.blog.dao.R;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//其他基础业务的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/common")
@Api(value = "其他基础业务的管理控制器",tags = "其他基础业务的管理控制器")
public class CommonController {

    @Resource
    private RabbitmqLogManager rabbitmqLogManager;

    //////////数据处理//////////

    //通过ramq日志紧急修复ramq
    @GetMapping("/fixramq")
    @ApiOperation(value = "通过ramq日志紧急修复ramq", notes = "管理员权限")
    public R<String> fixramq(){
        //权限判定
        if(!BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要管理员来进行，你无权操作");
        //正式执行
        log.info("正在执行紧急修复ramq业务");
        rabbitmqLogManager.fixRamq();
        //返回结果
        return R.success("紧急修复ramq成功");
    }


}
