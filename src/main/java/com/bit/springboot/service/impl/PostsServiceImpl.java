package com.bit.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.mapper.CommentsMapper;
import com.bit.springboot.mapper.FriendsMapper;
import com.bit.springboot.mapper.PostsMapper;
import com.bit.springboot.model.dto.CommentDTO;
import com.bit.springboot.model.entity.Comments;
import com.bit.springboot.model.vo.PostsDataVO;
import com.bit.springboot.model.entity.Posts;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.dto.PostsDataDTO;
import com.bit.springboot.service.PostsService;
import com.bit.springboot.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LeLe
 * @date 2024/5/25 17:05
 * @Description:
 */
@Service
@Slf4j
public class PostsServiceImpl  extends ServiceImpl<PostsMapper, Posts> implements PostsService{
    /**
     * 限流器
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(1.0);
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostsMapper postsMapper;
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private FriendsMapper friendsMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPosts(PostsDataDTO postsDataDTO, HttpServletRequest request) {
        if (rateLimiter.tryAcquire()){
            User user =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
            if (user ==null){
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            if (StrUtil.isBlank(postsDataDTO.getTitle()) || StrUtil.isBlank(postsDataDTO.getContent())){
                throw new BusinessException(ErrorCode.NULL_ADD);
            }
            if (StrUtil.isNotBlank(postsDataDTO.getImages())){
                String[] split = postsDataDTO.getImages().split("[，,]");
                String jsonStr = JSONUtil.toJsonStr(split);
                postsDataDTO.setImages(jsonStr);

            }
            if (StrUtil.isNotBlank(postsDataDTO.getTags())){
                String[] split = postsDataDTO.getTags().split("[，,]");
                String jsonStr2 = JSONUtil.toJsonStr(split);
                postsDataDTO.setTags(jsonStr2);
            }
            Posts posts = new Posts();
            BeanUtils.copyProperties(postsDataDTO,posts);
            posts.setUserId(user.getId());
            posts.setCreateTime(new Date());
            posts.setUserName(user.getUsername());
            posts.setAvatarUrl(user.getAvatarUrl());
            postsMapper.insertPosts(posts);
            List<Long> friendsId = friendsMapper.getFriendsId(user.getId());
            if (friendsId == null && friendsId.size() == 0){
                return;
            }
            PostsDataVO postsDataVO = new PostsDataVO();
            BeanUtils.copyProperties(posts,postsDataVO);
            Boolean like = like(posts, request);
            postsDataVO.setIsLike(like);
            friendsId.stream().forEach(id -> stringRedisTemplate.opsForList().leftPush(UserConstant.GET_POSTS_kEY + id,JSONUtil.toJsonStr(postsDataVO)));
            stringRedisTemplate.opsForList().leftPush(UserConstant.GET_POSTS_kEY + user.getId() ,JSONUtil.toJsonStr(postsDataVO));
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    @Override
    public List<PostsDataVO> listPosts(HttpServletRequest request) {
        Long postsList = stringRedisTemplate.opsForList().size("postsList");
        if (postsList != 0 ){
            List<String> list = stringRedisTemplate.opsForList().range("postsList", 0, -1);
           return list.stream().map(posts ->{
                PostsDataVO postsDataVO = JSONUtil.toBean(posts, PostsDataVO.class);
                Posts posts1 = new Posts();
                BeanUtils.copyProperties(posts,posts1);
                postsDataVO.setIsLike(like(posts1,request));
                return postsDataVO;
            }).collect(Collectors.toList());
        }
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("authority",1);
        List<Posts> list = list(queryWrapper);
        List<PostsDataVO> finalResultList = new ArrayList<>();
        list.stream().sorted((p1, p2) -> {
            return p2.getCreateTime().compareTo(p1.getCreateTime());
        }).forEach(posts -> {
            User user = userService.getById(posts.getUserId());
            PostsDataVO postsDataVO = new PostsDataVO();
            BeanUtils.copyProperties(posts,postsDataVO);
            postsDataVO.setIsLike(like(posts,request));
            stringRedisTemplate.opsForList().rightPush("postsList", JSONUtil.toJsonStr(postsDataVO));
            finalResultList.add(postsDataVO);
        });
        return finalResultList;
    }

    @Override
    public void likePosts(Long postsId, Long userId) {
        String key = "post_like_key:" + postsId;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        synchronized (userId.toString().intern()){
//            stringRedisTemplate.delete("postsList");
            if (score == null) {
                //未点赞则点赞数加1
                boolean resultValue = update().setSql("likeNumber = likeNumber + 1 ").eq("id", postsId).update();
                if (resultValue) {
                    stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
                }
            } else {
                // 4.1.数据库点赞数 -1
                boolean isSuccess = update().setSql("likeNumber = likeNumber - 1").eq("id", postsId).update();
                // 4.2.把用户从Redis的set集合移除
                if (isSuccess) {
                    stringRedisTemplate.opsForZSet().remove(key, userId.toString());
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComments(CommentDTO commentDTO) {
        if (StrUtil.isBlank(commentDTO.getAvatarUrl())){
            commentDTO.setAvatarUrl("https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg");
        }
        if (StrUtil.isBlank(commentDTO.getComment()) || StrUtil.isBlank(commentDTO.getAuthor())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Comments comments = new Comments();
        BeanUtils.copyProperties(commentDTO,comments);
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }

    @Override
    public List<PostsDataVO> getFriendsPosts(Long id) {
        Long size = stringRedisTemplate.opsForList().size(UserConstant.GET_POSTS_kEY + id);

        if (size != 0){
            List<String> stringList = stringRedisTemplate.opsForList().range(UserConstant.GET_POSTS_kEY + id, 0, -1);
            return stringList.stream().map(s -> {
                return JSONUtil.toBean(s, PostsDataVO.class);
            }).collect(Collectors.toList());
        }
        List<Posts> list = null;
        List<Long> friendsId = friendsMapper.getFriendsId(id);
        if (friendsId.size() != 0) {
            QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("userId",friendsId);
            list = list(queryWrapper);
        }

        QueryWrapper<Posts> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userId",id);
        List<Posts> mePostsList = list(queryWrapper1);
        if (list != null){
            mePostsList.addAll(list);
        }
        List<PostsDataVO> result = mePostsList.stream().sorted((p1, p2) -> {
            return p2.getCreateTime().compareTo(p1.getCreateTime());
        }).collect(Collectors.toList()).stream().map(posts -> {
            PostsDataVO postsDataVO = new PostsDataVO();
            BeanUtils.copyProperties(posts, postsDataVO);
            postsDataVO.setIsLike(like(posts,id));
            stringRedisTemplate.opsForList().rightPush(UserConstant.GET_POSTS_kEY + id,JSONUtil.toJsonStr(postsDataVO));
            return postsDataVO;
        }).collect(Collectors.toList());
        System.out.println(result);
        return result;
    }

    private Boolean like(Posts posts, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long id = posts.getId();
        String key = "post_like_key:" + id;
        if (user == null){
            return false;
        }
        Double score = stringRedisTemplate.opsForZSet().score(key, String.valueOf(user.getId()));
        Boolean result = score!=null;
        System.out.println(result);
        return result;
    }
    private Boolean like(Posts posts, Long userId) {
        Long id = posts.getId();
        String key = "post_like_key:" + id;
        Double score = stringRedisTemplate.opsForZSet().score(key, String.valueOf(userId));
        Boolean result = score!=null;
        System.out.println(result);
        return result;
    }
}
