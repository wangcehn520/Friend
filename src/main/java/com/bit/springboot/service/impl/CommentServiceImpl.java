package com.bit.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.springboot.mapper.CommentsMapper;
import com.bit.springboot.model.entity.Comments;
import com.bit.springboot.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * @author LeLe
 * @date 2024/5/31 14:03
 * @Description:
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentService {
}
