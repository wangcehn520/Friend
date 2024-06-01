package com.bit.springboot.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.springboot.model.dto.UserDTO;
import com.bit.springboot.model.dto.UserLoginDTO;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.dto.UserDataDTO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 86173
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-05-21 11:53:47
*/
public interface UserService extends IService<User> {

    /**
     * 登录接口
     * @param userLoginDTO
     * @param request
     * @return
     */
    UserDTO login(UserLoginDTO userLoginDTO,HttpServletRequest request);

    /**
     * 修改用户接口
     * @param userDTO
     * @param request
     */
    void updateUser(UserDTO userDTO, HttpServletRequest request);

    /**
     * 获取个人信息接口
     * @param request
     * @return
     */
    UserDTO getCurrent(HttpServletRequest request);

    void loginOut(HttpServletRequest request);

    void registerUser(UserDataDTO userDataDTO);
}
