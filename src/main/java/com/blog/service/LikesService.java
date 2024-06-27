package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Blog;
import com.blog.entity.Likes;

public interface LikesService extends IService<Likes> {

    //读取点赞状态
    Integer[] getLike(Long likesId, Long userId);

    //存储缓存的点赞
    void storeLike();
}
