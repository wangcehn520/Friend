package com.bit.springboot.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.common.ResultUtils;
import com.bit.springboot.constant.FriendConstant;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.model.entity.Friends;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.dto.AddFriendDTO;
import com.bit.springboot.model.vo.FriendDataVO;
import com.bit.springboot.service.FriendsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author LeLe
 * @date 2024/5/25 21:59
 * @Description:
 */
@RestController
@RequestMapping("/friend")
@Slf4j
@Api("好友相关操作")
public class FriendController {
    @Resource
    private FriendsService friendsService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/addFriend/{friendId}")
    public BaseResponse<String> addFriend(@PathVariable("friendId") Long friendId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (friendId == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
            stringRedisTemplate.opsForHash().put(FriendConstant.FRIEND_KEY_POST + user.getId(), String.valueOf(friendId), "0");
            stringRedisTemplate.opsForHash().put(FriendConstant.FRIEND_KEY_GET + friendId, String.valueOf(user.getId()), "0");

        return ResultUtils.success("ok");
    }

    @GetMapping("/searchFriends")
    public BaseResponse<List<FriendDataVO>> searchFriends(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
       List<FriendDataVO> resultList =  friendsService.searchFriends(user.getId());
        return ResultUtils.success(resultList);
    }

    @GetMapping("/delete/{friendId}")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> deleteFriend(@PathVariable("friendId") Long friendId,HttpServletRequest request){
        log.info("friendId:{}",friendId);
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //todo 可优化
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("friendId",friendId);
        friendsService.remove(queryWrapper);
        QueryWrapper<Friends> queryWrapper2 = new QueryWrapper<>();
        queryWrapper.eq("userId",friendId);
        friendsService.remove(queryWrapper2);
        return ResultUtils.success("ok");
    }

    @GetMapping("/agreeFriend/{friendId}")
    public BaseResponse<Integer> agreeFriend(@PathVariable("friendId") Long friendId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        AddFriendDTO addFriendDTO = new AddFriendDTO();
        addFriendDTO.setFriendId(friendId);
        addFriendDTO.setUserId(user.getId());
        friendsService.addFriend(addFriendDTO);
        return ResultUtils.success(1);
    }

    @GetMapping("/getAddFriendMessage")
    public BaseResponse<List<FriendDataVO>> getAddFriendMessage(HttpServletRequest request){
        List<FriendDataVO> result=friendsService.getFriendMessage(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/getSendMessage")
    public BaseResponse<List<FriendDataVO>> getSendMessage(HttpServletRequest request){
        List<FriendDataVO> resultList = friendsService.getSendMessage(request);
        return ResultUtils.success(resultList);
    }

    @GetMapping("/refuse/{friendId}")
    public BaseResponse<String> refuse (@PathVariable("friendId") Long friendId , HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        stringRedisTemplate.opsForHash().put(FriendConstant.FRIEND_KEY_POST + friendId, String.valueOf(user.getId()), "2");
        stringRedisTemplate.opsForHash().put(FriendConstant.FRIEND_KEY_GET + user.getId(),String.valueOf(friendId), "2");
        return ResultUtils.success("ok");
    }

}
