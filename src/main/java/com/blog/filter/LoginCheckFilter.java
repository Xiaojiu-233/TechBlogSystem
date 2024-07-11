package com.blog.filter;

import com.alibaba.fastjson.JSON;
import com.blog.entity.Emp;
import com.blog.entity.User;
import com.blog.service.EmpService;
import com.blog.service.UserService;
import com.blog.utils.BaseContext;
import com.blog.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//检测用户是否已经登录完成
@Slf4j
@Component
@WebFilter(filterName = "LoginCheckFilter" , urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private EmpService empService;

    //执行过滤器程序
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求URI
        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径
        String[] urls = {
                "/blog/user/login",//用户的登录登出注册
                "/blog/user",
                "/blog/user/logout",
                "/blog/emp/login",//员工的登录登出
                "/blog/emp/logout",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        //添加请求头解决cors问题
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Methods", "*");

        //对于不需要处理的路径，直接放行
        if(check(urls,requestURI)){
            log.info("本次请求：{} 无需处理，正在放行",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，登录了则放行
        boolean success = true;
        //检查登录凭证与redis缓存，确认系统是否登记
        BaseContext.setIsAdmin(false);
        Long targetId = null;
        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies == null) success = false;
        else{
            for( Cookie c :cookies){
                //员工凭证优先级大于用户凭证，员工凭证能用于修改数据
                if(c.getName().equals("empLogin") ){
                    BaseContext.setIsAdmin(true);
                    token = c.getValue();
                    break;
                }
                if(c.getName().equals("userLogin") ){
                    token = c.getValue();
                }
            }
            if(token == null) success = false;
            else{
                ValueOperations op = redisTemplate.opsForValue();
                String res = (String) op.get(token);
                if(res == null)success = false;
                else targetId = Long.valueOf(res);
            }
        }

        //根据结果判定登录是否成功
        if(success){
            //如果是今天第一次登录，则记录登录时间
            LocalDateTime now = LocalDateTime.now();
            if(BaseContext.getIsAdmin()){
                Emp emp = empService.getById(targetId);
                LocalDateTime time = emp.getLoginTime();
                if(time == null || ChronoUnit.DAYS.between(time,now) >= 1){
                    emp.setLoginTime(now);
                    empService.updateById(emp);
                }
            }else{
                User user = userService.getById(targetId);
                LocalDateTime time = user.getLoginTime();
                if(time == null || ChronoUnit.DAYS.between(time,now) >= 1){
                    user.setLoginTime(now);
                    userService.updateById(user);
                }
            }
            //登录成功
            log.info("本次请求为用户ID：{} 发起，允许通行",targetId);
            BaseContext.setCurrentId(targetId);
            filterChain.doFilter(request,response);
            return;
        }

        //如果该用户未登录或者登录凭证无效，禁止放行并返回警告
        log.info("本次请求没有有效凭证，已向客户端发出警告");
        response.setContentType("text/html; charset=utf-8");
        response.getWriter().write(JSON.toJSONString(R.failure("很抱歉，由于您没有有效的凭证，系统拒绝了您的请求，请尝试登录后访问")));
    }

    //路径匹配，检查本次请求是否需要放行
    public boolean check(String[] urls,String requestURI){
        for (String url:urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match)return true;
        }
        return false;
    }
}
