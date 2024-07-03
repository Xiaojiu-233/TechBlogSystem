package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.Mail;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.MailMapper;
import com.blog.service.BlogService;
import com.blog.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {
}
