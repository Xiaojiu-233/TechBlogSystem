package com.blog.utils;

import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RedisUtil {

    public static void delRedisCache(HttpServletRequest request, RedisTemplate redisTemplate,String delCacheName){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cc : cookies)
                if(cc.getName().equals(delCacheName)){
                    redisTemplate.delete(cc.getValue());
                }
        }
    }
}
