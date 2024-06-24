package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Blog;
import com.blog.entity.Emp;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.EmpMapper;
import com.blog.service.BlogService;
import com.blog.service.EmpService;

public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp> implements EmpService {
}
