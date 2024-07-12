package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.utils.EncryptUtil;
import com.blog.dao.R;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//用户的管理控制器
@Slf4j
@RestController
@RequestMapping("/blog/user")
@Api(value = "用户的管理控制器",tags = "用户的管理控制器")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisTemplate redisTemplate_3;

    @Resource
    private RabbitTemplate rabbitTemplate;

    //////////业务处理//////////

    //用户登录
    @PostMapping("/login")
    @ApiOperation(value = "用户的登录", notes = "body输入username和password，获得登录凭证")
    public R<String> login(@RequestBody Map map, HttpServletRequest request, HttpServletResponse response){
        //获取数据
        String username = (String) map.get("username");
        String password = EncryptUtil.MD5Encrypt((String) map.get("password"));
        //正式执行
        log.info("正在进行用户登录业务处理 查询用户名={}",username);

        //构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(User::getUsername,username);
        //搜寻，得到目标，返回密码
        User user = userService.getOne(queryWrapper);

        //用户存在性检测
        if(user == null )return R.failure("登录失败！原因为该用户不存在");

        //用户是否已经封禁
        if(user.getIsLock() != 0 )return R.failure("登录失败！原因为该用户已被封禁");

        //通过redis读取登录失败次数
        ValueOperations fop = redisTemplate_3.opsForValue();
        Integer failCount = fop.get("user::" + username) == null ? null : Integer.parseInt((String) fop.get("user::" + username)) ;
        //如果失败次数超过三次，则告诉用户
        if(failCount != null && failCount >= 3)return R.failure("登录失败！原因为失败登录次数超过三次，需等待60秒");

        //密码检测
        String password_get = user.getPassword();
        if (!password_get.equals(password)){
            //失败后，如果没有失败次数则添加失败次数，有则失败次数+1
            if(failCount != null && failCount >= 2)
                fop.set("user::" + username,String.valueOf(failCount != null ? failCount+1 : 1),60, TimeUnit.SECONDS);
            //返回结果
            return R.failure("登录失败！原因为密码错误");
        }

        //登陆成功，失败次数清零
        redisTemplate_3.delete(username);

        //清除redis凭证缓存
        RedisUtil.delRedisCache(request,redisTemplate,"userLogin");
        //如果是今天第一次登录，则记录登录时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time = user.getLoginTime();
        if(time == null || ChronoUnit.DAYS.between(time,now) >= 1){
            user.setLoginTime(now);
            userService.updateById(user);
        }
        //登录成功，将登录凭证交给cookie与redis
        String code = UUID.randomUUID().toString();
        Cookie c = new Cookie("userLogin",code);
        c.setMaxAge(3600*24*7);
        c.setPath("/blog");
        response.addCookie(c);
        ValueOperations op = redisTemplate.opsForValue();
        op.set(code,String.valueOf(user.getId()),7, TimeUnit.DAYS);

        return R.success("登录成功！");

    }

    //用户退出登录
    @PostMapping("/logout")
    @ApiOperation(value = "用户的退出登录", notes = "消除登录凭证，退出登录")
    public R<String> logout(HttpServletRequest request,HttpServletResponse response){
        //清除cookie
        Cookie c = new Cookie("userLogin","");
        c.setMaxAge(0);
        c.setPath("/blog");
        response.addCookie(c);
        //清除redis凭证缓存
        RedisUtil.delRedisCache(request,redisTemplate,"userLogin");
        return R.success("退出登录成功！");
    }

    //////////数据处理//////////

    //用户的分页查询(可以通过名字查询)
    @GetMapping("/page")
    @ApiOperation(value = "用户的分页查询", notes = "可以通过名字查询")
    public R<Page> page(int page,int pageSize,String name){
        log.info("正在进行分页查询 页数={} 页大小={} 查询用户名={}",page,pageSize,name);

        //构造分页构造器
        Page<User> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(name),User::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(User::getRegisterTime);
        //分页查询，结果返回给pageInfo
        userService.page(pageInfo,queryWrapper);

        //部分数据不予公开，如：密码
        List<User> userRecords = pageInfo.getRecords();
        userRecords = userRecords.stream().peek(user -> user.setPassword("")).collect(Collectors.toList());
        pageInfo.setRecords(userRecords);

        //返回结果
        return R.success(pageInfo);
    }

    //用户的id查询
    @GetMapping("/{id}")
    @ApiOperation(value = "用户的id查询", notes = "用户的id查询")
    public R<User> getById(@PathVariable("id")Long id){
        //正式执行
        log.info("正在执行用户的按id获取：{}",id);
        //获取用户
        User user= userService.getById(id);
        //排除异常情况
        if(user==null)return R.failure("用户查询失败");
        //清除密码等隐蔽信息
        user.setPassword("");
        //返回结果
        return R.success(user);
    }

    //用户注册
    @PostMapping
    @ApiOperation(value = "用户注册", notes = "创建新的用户")
    public R<String> register(@RequestBody User user){
        //正式执行
        log.info("正在执行用户的注册: {}",user);
        //如果拥有用户名相同的用户，则增加失败
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        if (userService.count(queryWrapper)>0) return R.failure("用户用户名有重复");
        //特殊条件判定
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName,user.getName());
        if (userService.count(queryWrapper)>0) return R.failure("用户昵称有重复");
        if(user.getPassword().isEmpty()) return R.failure("用户未设置密码");
        if(!user.getPassword().matches("^[0-9a-zA-Z_]{8,16}$")) return R.failure("用户密码不符合：只有字母数字下划线、长度8-16位");
        //添加用户
        user.setId(IdWorker.getId());
        user.setPassword(EncryptUtil.MD5Encrypt(user.getPassword()));
        user.setHeadImg("");
        boolean success = userService.save(user);
        //返回结果
        return success ? R.success("用户注册成功") : R.failure("用户注册失败");
    }

    //修改指定id用户信息
    @PostMapping("/upd")
    @ApiOperation(value = "修改指定id用户信息", notes = "修改自己的信息")
    public R<String> updateById( User user){
        //权限判定
        if(BaseContext.getIsAdmin() || !Objects.equals(BaseContext.getCurrentId(), user.getId()))
            return R.failure("该id不是你的用户id，你无权操作");
        //正式执行
        log.info("正在修改用户的信息: 用户id={},用户信息={}",user.getId(),user);
        //修改用户信息
        User u = userService.getById(user.getId());
        if(u == null) return R.failure("用户的id不存在");
        user.setPassword(u.getPassword());
        boolean success = userService.updateById(user);
        //返回结果
        return success ? R.success("修改用户信息成功") : R.failure("修改用户信息失败");
    }

    //修改指定id用户密码
    @PostMapping("/updPwd")
    @ApiOperation(value = "修改指定id用户密码", notes = "修改自己的密码")
    public R<String> updatePwdById(Long userId,String oldPwd,String newPwd){
        //权限判定
        if(BaseContext.getIsAdmin() || !Objects.equals(BaseContext.getCurrentId(), userId))
            return R.failure("该id不是你的用户id，你无权操作");
        //正式执行
        log.info("正在修改用户的密码: 用户id={},老密码={},新密码={}",userId,oldPwd,newPwd);
        //新密码判定
        if(newPwd.isEmpty()) return R.failure("用户未设置新密码");
        if(newPwd.equals(oldPwd))  return R.failure("用户新密码与老密码一致");
        if(!newPwd.matches("^[0-9a-zA-Z_]{8,16}$")) return R.failure("用户新密码不符合：只有字母数字下划线、长度8-16位");
        //老密码判定
        oldPwd = EncryptUtil.MD5Encrypt(oldPwd);
        User u = userService.getById(userId);
        if(u == null) return R.failure("用户的id不存在");
        if(!u.getPassword().equals(oldPwd)) return R.failure("用户的老密码错误");
        //修改用户密码
        newPwd = EncryptUtil.MD5Encrypt(newPwd);
        u.setPassword(newPwd);
        boolean success = userService.updateById(u);
        //返回结果
        return success ? R.success("修改用户密码成功") : R.failure("修改用户密码失败");
    }

    //封禁用户
    @PostMapping("/lock")
    @ApiOperation(value = "封禁指定id用户，day=-1时永封", notes = "管理员权限")
    public R<String> lockById(Long userId,Integer days){
        //权限判定
        if(!BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要管理员权限，你无权操作");
        //正式执行
        log.info("正在封禁用户: 用户id={} 时间={}",userId,days);
        //查看有没有该用户
        User user = userService.getById(userId);
        if(user == null)return R.failure("未找到相关用户");
        //消息队列发送延迟队列以进行延时解封（day=-1时不用管）
        Long lockTime = System.currentTimeMillis();
        if(days != -1){
            //如果之前存在封禁情况的话，删除之前的队列消息
            Long lockedTime = user.getIsLock();
            Long lockUntil = lockTime + 1000L * 3600 * 24 * days;
            if(lockedTime != 0)rabbitTemplate.convertAndSend("CancelExchange","CancelForbidRouting",lockedTime);
            //添加现有的新消息
            rabbitTemplate.convertAndSend("ForbidExchange", "UserForbidRouting", userId + "::" + lockUntil, message -> {
                message.getMessageProperties().setExpiration(String.valueOf(lockTime));
                message.getMessageProperties().setMessageId(lockTime.toString());
                return message;
            });
        }
        //封禁与返回结果
        user.setIsLock(lockTime);
        return userService.updateById(user) ? R.success("用户封禁成功") : R.failure("用户封禁失败");
    }

    //解封用户（手动）
    @PostMapping("/unlock")
    @ApiOperation(value = "解封指定id用户", notes = "管理员权限")
    public R<String> unlockById(Long userId){
        //权限判定
        if(!BaseContext.getIsAdmin() || BaseContext.getCurrentId() == null)
            return R.failure("该操作需要管理员权限，你无权操作");
        //正式执行
        log.info("正在解封用户: 用户id={}",userId);
        //查看有没有该用户
        User user = userService.getById(userId);
        if(user == null)return R.failure("未找到相关用户");
        //删除消息队列里延迟队列消息
        Long lockedTime = user.getIsLock();
        if(lockedTime != 0)rabbitTemplate.convertAndSend("CancelExchange","CancelForbidRouting",lockedTime);
        //解封与返回结果
        user.setIsLock(0L);
        return userService.updateById(user) ? R.success("用户解封成功") : R.failure("用户解封失败");
    }
}
