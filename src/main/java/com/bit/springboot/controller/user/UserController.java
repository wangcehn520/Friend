package com.bit.springboot.controller.user;

import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.common.ResultUtils;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.mapper.FriendsMapper;
import com.bit.springboot.model.dto.UserDTO;
import com.bit.springboot.model.entity.Friends;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.dto.AddFriendDTO;
import com.bit.springboot.model.dto.UserDataDTO;
import com.bit.springboot.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author LeLe
 * @date 2024/5/22 13:58
 * @Description:
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api("用户的增删改查")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FriendsMapper friendsMapper;

    @PostMapping("/register")
    public BaseResponse<String> register(@RequestBody UserDataDTO userDataDTO){
        userService.registerUser(userDataDTO);
        return ResultUtils.success("ok");
    }

    @GetMapping("/searchOthers/{userId}")
    public BaseResponse<UserDTO> getUser(@PathVariable("userId") Long userId, HttpServletRequest request){
        User user1 =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        UserDTO userDTO = new UserDTO();
        User user = userService.getById(userId);
        BeanUtils.copyProperties(user,userDTO);
        if (user1 == null){
            userDTO.setIsFriend(false);
            return ResultUtils.success(userDTO);
        }
        if (user1.getId().equals(userId)){
            userDTO.setIsMe(true);
        }
        AddFriendDTO addFriendDTO = new AddFriendDTO();
        addFriendDTO.setFriendId(userId);
        addFriendDTO.setUserId(user1.getId());
        Friends friends = friendsMapper.searchFriends(addFriendDTO);
        if (friends == null){
            return ResultUtils.success(userDTO);
        }
        userDTO.setIsFriend(true);
        return ResultUtils.success(userDTO);
    }
}
