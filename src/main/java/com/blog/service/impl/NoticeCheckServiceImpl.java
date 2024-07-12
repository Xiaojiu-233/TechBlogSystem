package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Mail;
import com.blog.entity.NoticeCheck;
import com.blog.mapper.MailMapper;
import com.blog.mapper.NoticeCheckMapper;
import com.blog.service.MailService;
import com.blog.service.NoticeCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NoticeCheckServiceImpl extends ServiceImpl<NoticeCheckMapper, NoticeCheck> implements NoticeCheckService {
}
