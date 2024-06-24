package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.UserColl;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.UserCollMapper;
import com.blog.service.BlogService;
import com.blog.service.UserCollService;

public class UserCollServiceImpl extends ServiceImpl<UserCollMapper, UserColl> implements UserCollService {
}
