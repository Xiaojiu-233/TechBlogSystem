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

//其他基础业务的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/common")
@Api(value = "其他基础业务的管理控制器",tags = "其他基础业务的管理控制器")
public class CommonController {

    //读取配置文件的自定义参数
    @Value("${tourist.path}")
    private String basePath;
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

    //文件上传
    @PostMapping("/upload/{cate}")
    @ApiOperation(value = "上传", notes = "返回数据为上传成功的文件名")
    public R<String> upload(MultipartFile file, @PathVariable("cate") String category){
        //file是一个临时文件，需要将该文件转存至目录中，否则本次请求执行过后file将删除
        log.info(file.toString());

        //原始文件名 获取后缀
        String originFileName = file.getOriginalFilename();
        String suffix = originFileName.substring(originFileName.lastIndexOf('.'));

        //设置uuid作为新的文件名，以防止文件名重复
        String newFileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象 如果没有目录则创建目录
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        //如果存在分类的String，将分类路径放置在新文件名中
        if(category != null)newFileName = category + "/" + newFileName;

        try{
            //将临时文件转存到指定目录
            file.transferTo(new File(basePath + newFileName));
        }catch (Exception e){
            System.out.println(e);
        }

        return R.success("?name="+newFileName);
    }

    //文件下载
    @GetMapping("/download")
    @ApiOperation(value = "下载", notes = "输入文件名（带分类路径）")
    public void download(String name, HttpServletResponse response) {

        try{
            //对文件名进行处理，减少被攻击风险
            name = name.replace("/..","");
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath+name);
            //通过输出流将内容写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应的ContentType
            response.setContentType("image/jpeg");
            //读取文件
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭输出输入流
            outputStream.close();
            fileInputStream.close();

        }catch (Exception e){
            //返回异常
            try{ response.getWriter().write(e.toString());
            }catch (Exception ee){}
            log.info("出现异常！" + e);
        }

    }
}
