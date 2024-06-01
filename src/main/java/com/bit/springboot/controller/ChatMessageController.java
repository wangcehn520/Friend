package com.bit.springboot.controller;

import cn.hutool.json.JSONUtil;
import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.common.ResultUtils;
import com.bit.springboot.constant.MessageConstant;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.vo.FriendDataVO;
import com.bit.springboot.utils.GenerateUUIDUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author LeLe
 * @date 2024/5/30 17:01
 * @Description:
 */
@RestController
@RequestMapping("/chat")
@Slf4j
@Api("获取历史信息")
public class ChatMessageController {
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 获取历史信息
     * @param friendId
     * @param request
     * @return
     */
    @GetMapping("/message/{friendId}")
    public BaseResponse<List<String>> getMessages(@PathVariable("friendId") String friendId, HttpServletRequest request){
        User user1 =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user1 == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String uuid = GenerateUUIDUtils.generateUniqueId(user1.getId(), Long.valueOf(friendId));
        List<String> list = redisTemplate.opsForList().range(MessageConstant.PRIVATE_MESSAGE_KEY + uuid, 0, -1);
        return ResultUtils.success(list);
    }

    @GetMapping("/RecentlyContacted/{userId}")
    public  BaseResponse<List<FriendDataVO>> RecentlyContacted(@PathVariable("userId") Long userId,HttpServletRequest request){
        User user =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<FriendDataVO> resultList = new ArrayList<>();
        Set<Object> keys = redisTemplate.opsForHash().keys(MessageConstant.RECENTLY_CONTACTED_KEY + user.getId());
        if (keys.size() == 0){
            ResultUtils.success(null);
        }
        Object[] objects = keys.toArray();
        for (Object key : objects) {
            String str =(String)redisTemplate.opsForHash().get(MessageConstant.RECENTLY_CONTACTED_KEY + user.getId(), key);
            FriendDataVO friendDataVO = JSONUtil.toBean(str, FriendDataVO.class);
            resultList.add(friendDataVO);
        }
        return ResultUtils.success(resultList);
    }

}
