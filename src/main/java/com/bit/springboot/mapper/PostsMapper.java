package com.bit.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bit.springboot.model.entity.Posts;

/**
 * @author LeLe
 * @date 2024/5/25 17:06
 * @Description:
 */
public interface PostsMapper extends BaseMapper<Posts> {
    int insertPosts(Posts posts);
}
