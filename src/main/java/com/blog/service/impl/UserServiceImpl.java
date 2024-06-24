package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.User;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.BlogService;
import com.blog.service.UserService;

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
