package com.blog.controller;

import com.blog.component.RabbitmqLogManager;
import com.blog.dao.R;
import com.blog.utils.BaseContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

//页面跳转的管理控制器
@Slf4j
@RestController
@RequestMapping("/")
@Api(value = "页面跳转的管理控制器",tags = "页面跳转的管理控制器")
public class IndexController {

    //////////业务处理//////////

    //进入前台
    @RequestMapping("/")
    public RedirectView jumpIndex(){
        return new RedirectView("/front/html/login.html");
    }

}
