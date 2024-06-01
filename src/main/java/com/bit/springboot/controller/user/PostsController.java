package com.bit.springboot.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.common.ResultUtils;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.mapper.FriendsMapper;
import com.bit.springboot.model.dto.CommentDTO;
import com.bit.springboot.model.entity.Comments;
import com.bit.springboot.model.entity.Friends;
import com.bit.springboot.model.entity.Posts;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.vo.PostsDataVO;
import com.bit.springboot.model.dto.PostsDataDTO;
import com.bit.springboot.service.CommentService;
import com.bit.springboot.service.FriendsService;
import com.bit.springboot.service.PostsService;
import com.bit.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author LeLe
 * @date 2024/5/25 14:27
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/posts")
public class PostsController {
    @Autowired
    private PostsService postsService;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommentService commentService;



    @PostMapping("/add")
    public BaseResponse<String> addPosts(@RequestBody PostsDataDTO postsDataDTO, HttpServletRequest request){
        log.info("posts:{}", postsDataDTO);
        postsService.addPosts(postsDataDTO,request);
        return ResultUtils.success("ok");
    }

    @GetMapping("/list")
    public BaseResponse<List<PostsDataVO>> list(HttpServletRequest request){
        List<PostsDataVO> resultList = postsService.listPosts(request);
        return ResultUtils.success(resultList);
    }

    @GetMapping("/like/{postsId}")
    public BaseResponse<String> likePosts (@PathVariable("postsId")Long postsId,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        postsService.likePosts(postsId,user.getId());
        return ResultUtils.success("ok");
    }

    @GetMapping("/delete/{postsId}")
    public BaseResponse<String> deletePosts(@PathVariable("postsId") Long postsId){
        return null;
    }

    @GetMapping("/getById/{postsId}")
    public BaseResponse<PostsDataVO> getById(@PathVariable("postsId") Long postsId,HttpServletRequest request){
        String key = "post_like_key:" + postsId;
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        PostsDataVO postsDataVO = new PostsDataVO();
        Posts posts = postsService.getById(postsId);
        User userById = userService.getById(posts.getUserId());
        BeanUtils.copyProperties(posts,postsDataVO);
        postsDataVO.setUserName(userById.getUsername());
        postsDataVO.setAvatarUrl(userById.getAvatarUrl());
        if (user == null){
            postsDataVO.setIsLike(false);
        }else {
            Double score = stringRedisTemplate.opsForZSet().score(key, String.valueOf(user.getId()));
            Boolean result = score!=null;
            postsDataVO.setIsLike(result);
        }
        return ResultUtils.success(postsDataVO);
    }

    @PostMapping("/comment")
    public BaseResponse<String> comments(@RequestBody CommentDTO commentDTO,HttpServletRequest request){
        log.info("commentDTO:{}",commentDTO);
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        postsService.saveComments(commentDTO);
        return ResultUtils.success("ok");
    }

    @GetMapping("/getComment/{postsId}")
    public BaseResponse<List<Comments>> getComment(@PathVariable("postsId") Long postsId){
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postsId",postsId);
        List<Comments> list = commentService.list(queryWrapper);
        return ResultUtils.success(list);
    }

    @GetMapping("/getFriendsPosts")
    public BaseResponse<List<PostsDataVO>> getFriendsPosts(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<PostsDataVO> result = postsService.getFriendsPosts(user.getId());
        return ResultUtils.success(result);
    }
}
