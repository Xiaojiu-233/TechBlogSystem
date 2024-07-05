package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Emp;
import com.blog.entity.User;
import com.blog.service.EmpService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.utils.EncryptUtil;
import com.blog.utils.R;
import com.blog.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//管理员的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/emp")
@Api(value = "管理员的管理控制器",tags = "管理员的管理控制器")
public class EmpController {

    @Resource
    private EmpService empService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisTemplate redisTemplate_3;

    @Resource
    private RabbitTemplate rabbitTemplate;

    //////////业务处理//////////

    //管理员登录
    @PostMapping("/login")
    @ApiOperation(value = "管理员的登录", notes = "body输入username和password，获得登录凭证")
    public R<String> login(@RequestBody Map map, HttpServletRequest request, HttpServletResponse response){
        //获取数据
        String username = (String) map.get("username");
        String password = EncryptUtil.MD5Encrypt((String) map.get("password"));
        //正式执行
        log.info("正在进行管理与登录业务处理 查询管理员名={}",username);

        //构造条件构造器
        LambdaQueryWrapper<Emp> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(Emp::getUsername,username);
        //搜寻，得到目标，返回密码
        Emp emp = empService.getOne(queryWrapper);

        //管理员存在性检测
        if(emp == null )return R.failure("登录失败！原因为该管理员不存在");

        //通过redis读取登录失败次数
        ValueOperations fop = redisTemplate_3.opsForValue();
        Integer failCount = fop.get("emp::" + username) == null ? null : Integer.parseInt((String) fop.get("emp::" + username)) ;
        //如果失败次数超过三次，则告诉用户
        if(failCount != null && failCount > 3)return R.failure("登录失败！原因为失败登录次数超过三次，需等待60秒");

        //密码检测
        String password_get = emp.getPassword();
        if (!password_get.equals(password)){
            //失败后，如果没有失败次数则添加失败次数，有则失败次数+1
            fop.set("emp::" + username,String.valueOf(failCount != null ? failCount+1 : 1),7, TimeUnit.DAYS);
            //失败三次之后直接给rabbitmq丢延时消息
            if(failCount != null && failCount >= 3)
            rabbitTemplate.convertAndSend("ForbidAndLoginExchange", "LoginFailRouting","emp::" + username);
            //返回结果
            return R.failure("登录失败！原因为密码错误");
        }

        //登陆成功，失败次数清零
        redisTemplate_3.delete(username);

        //清除redis凭证缓存
        RedisUtil.delRedisCache(request,redisTemplate,"empLogin");
        //登录成功，将登录凭证交给cookie与redis
        String code = UUID.randomUUID().toString();
        Cookie c = new Cookie("empLogin",code);
        c.setMaxAge(3600*24*7);
        c.setPath("/blog");
        response.addCookie(c);
        ValueOperations op = redisTemplate.opsForValue();
        op.set(code,String.valueOf(emp.getId()),7, TimeUnit.DAYS);

        return R.success("登录成功！");

    }

    //管理员退出登录
    @PostMapping("/logout")
    @ApiOperation(value = "管理员的退出登录", notes = "消除登录凭证，退出登录")
    public R<String> logout(HttpServletRequest request,HttpServletResponse response){
        //清除cookie
        Cookie c = new Cookie("empLogin","");
        c.setMaxAge(0);
        c.setPath("/blog");
        response.addCookie(c);
        //清除redis凭证缓存
        RedisUtil.delRedisCache(request,redisTemplate,"empLogin");
        return R.success("退出登录成功！");
    }

    //////////数据处理//////////

    //管理员的id查询
    @GetMapping("/{id}")
    @ApiOperation(value = "管理员的id查询", notes = "管理员的id查询")
    public R<Emp> getById(@PathVariable("id")Long id){
        //正式执行
        log.info("正在执行管理员的按id获取：{}",id);
        //获取管理员
        Emp emp= empService.getById(id);
        //排除异常情况
        if(emp==null)return R.failure("管理员查询失败");
        //清除密码等隐蔽信息
        emp.setPassword("");
        //返回结果
        return R.success(emp);
    }

    //管理员注册
    @PostMapping
    @ApiOperation(value = "管理员注册", notes = "创建新的管理员，需要超级管理员root权限")
    public R<String> register(@RequestBody Emp emp){
        //权限判定
        if(!BaseContext.getIsAdmin() || !BaseContext.getCurrentId().equals(1L))
            return R.failure("该操作需要超级管理员来进行，你无权操作");
        //正式执行
        log.info("正在执行管理员的注册: {}",emp);
        //如果拥有管理员名相同的管理员，则增加失败
        LambdaQueryWrapper<Emp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Emp::getUsername,emp.getUsername());
        if (empService.count(queryWrapper)>0) return R.failure("管理员管理员名有重复");
        //特殊条件判定
        if(emp.getPassword().isEmpty()) return R.failure("管理员未设置密码");
        if(!emp.getPassword().matches("^[0-9a-zA-Z_]{8,16}$")) return R.failure("管理员密码不符合：只有字母数字下划线、长度8-16位");
        //添加管理员
        emp.setId(IdWorker.getId());
        emp.setPassword(EncryptUtil.MD5Encrypt(emp.getPassword()));
        boolean success = empService.save(emp);
        //返回结果
        return success ? R.success("管理员注册成功") : R.failure("管理员注册失败");
    }

    //修改指定id管理员密码
    @PostMapping("/updPwd")
    @ApiOperation(value = "修改指定id管理员密码", notes = "修改密码，需要超级管理员root权限")
    public R<String> updatePwdById(Long empId,String oldPwd,String newPwd){
        //权限判定
        if(!BaseContext.getIsAdmin() || !BaseContext.getCurrentId().equals(1L))
            return R.failure("该操作需要超级管理员来进行，你无权操作");
        //正式执行
        log.info("正在修改管理员的密码: 管理员id={},老密码={},新密码={}",empId,oldPwd,newPwd);
        //新密码判定
        if(newPwd.isEmpty()) return R.failure("管理员未设置新密码");
        if(newPwd.equals(oldPwd))  return R.failure("管理员新密码与老密码一致");
        if(!newPwd.matches("^[0-9a-zA-Z_]{8,16}$")) return R.failure("管理员新密码不符合：只有字母数字下划线、长度8-16位");
        //老密码判定
        oldPwd = EncryptUtil.MD5Encrypt(oldPwd);
        Emp e = empService.getById(empId);
        if(e == null) return R.failure("管理员的id不存在");
        if(!e.getPassword().equals(oldPwd)) return R.failure("管理员的老密码错误");
        //修改管理员密码
        newPwd = EncryptUtil.MD5Encrypt(newPwd);
        e.setPassword(newPwd);
        boolean success = empService.updateById(e);
        //返回结果
        return success ? R.success("修改管理员密码成功") : R.failure("修改管理员密码失败");
    }

    //删除管理员
    @PostMapping("/del")
    @ApiOperation(value = "删除指定id管理员", notes = "需要超级管理员root权限，不能删除root")
    public R<String> lockById(Long empId){
        //权限判定
        if(!BaseContext.getIsAdmin() || !BaseContext.getCurrentId().equals(1L))
            return R.failure("该操作需要超级管理员来进行，你无权操作");
        //无法删除root
        if(empId == 1L)
            return R.failure("你不能删除超级管理员！");
        //正式执行
        log.info("正在封禁管理员: 管理员id={}",empId);
        //查看有没有该管理员
        Emp emp = empService.getById(empId);
        if(emp == null)return R.failure("未找到相关管理员");
        //封禁与返回结果
        return empService.removeById(empId) ? R.success("管理员封禁成功") : R.failure("管理员封禁失败");
    }


}
