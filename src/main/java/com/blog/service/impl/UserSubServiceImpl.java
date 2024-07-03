package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.UserSub;
import com.blog.mapper.UserSubMapper;
import com.blog.service.UserSubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserSubServiceImpl extends ServiceImpl<UserSubMapper, UserSub> implements UserSubService {
}
