package com.bit.springboot.controller.user;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.common.ResultUtils;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.model.dto.UserDTO;
import com.bit.springboot.model.dto.UserLoginDTO;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Wrapper;

/**
 * @author LeLe
 * @date 2024/5/22 13:48
 * @Description:
 */

@RestController
@RequestMapping("/user")
@Api("用户登录")
//@CrossOrigin
public class UserLoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<UserDTO> login(@RequestBody UserLoginDTO userLoginDTO,HttpServletRequest request){
        if ("null".equals(userLoginDTO.getUserAccount())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StrUtil.isBlank(userLoginDTO.getUserAccount()) && StrUtil.isBlank(userLoginDTO.getUserPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
       UserDTO userDTO=userService.login(userLoginDTO,request);

        return ResultUtils.success(userDTO);
    }


    @GetMapping("/current")
    @ApiOperation("获取当前用户")
    public BaseResponse<UserDTO> getCurrent(HttpServletRequest request) throws ParseException {
        UserDTO current = userService.getCurrent(request);
        return ResultUtils.success(current);
    }

    @PostMapping("/update")
    @ApiOperation("修改用户")
    public BaseResponse<String> update(@RequestBody UserDTO userDTO,HttpServletRequest request){
        userService.updateUser(userDTO,request);
        return ResultUtils.success("");
    }

    @GetMapping("/loginout")
    @ApiOperation("用户退出")
    public BaseResponse<String> loginOut(HttpServletRequest request){
        userService.loginOut(request);
        return ResultUtils.success("");
    }

}
