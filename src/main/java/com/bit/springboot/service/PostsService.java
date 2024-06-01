package com.bit.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.springboot.model.dto.CommentDTO;
import com.bit.springboot.model.vo.PostsDataVO;
import com.bit.springboot.model.entity.Posts;
import com.bit.springboot.model.dto.PostsDataDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostsService extends IService<Posts> {
    void addPosts(PostsDataDTO postsDataDTO, HttpServletRequest request);

    /**
     * 获取帖子
     * @return
     */
    List<PostsDataVO> listPosts(HttpServletRequest request);

    /**
     * 点赞
     * @param postsId
     * @param userId
     */
    void likePosts(Long postsId, Long userId);

    /**
     * 评论
     * @param commentDTO
     */
    void saveComments(CommentDTO commentDTO);

    List<PostsDataVO> getFriendsPosts(Long id);
}
