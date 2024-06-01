package com.bit.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.mapper.UserMapper;
import com.bit.springboot.model.dto.UserDTO;
import com.bit.springboot.model.dto.UserLoginDTO;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.dto.UserDataDTO;
import com.bit.springboot.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author 86173
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-05-21 11:53:47
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 限流器
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(1.0);

    @Override
    public UserDTO login(UserLoginDTO userLoginDTO,HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userLoginDTO.getUserAccount());
        queryWrapper.eq("userPassword", DigestUtils.md5DigestAsHex(userLoginDTO.getUserPassword().getBytes(StandardCharsets.UTF_8)));
        User user = getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        UserDTO userDTO = setUerDTOAndRedis(user,false);

        return userDTO;
    }

    @Override
    public void updateUser(UserDTO userDTO, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user.getId()==null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        User user1 = new User();
        if (user.getId().equals(userDTO.getId())){
            BeanUtils.copyProperties(userDTO,user1);
            userMapper.updateUser(user1);
            Set<Object> keys = stringRedisTemplate.opsForHash().keys(UserConstant.USER_LOGIN_STATE + String.valueOf(user.getId()));
            Object[] objects = keys.toArray();
            for (Object object : objects) {
                stringRedisTemplate.opsForHash().delete(UserConstant.USER_LOGIN_STATE + String.valueOf(user.getId()),object);
            }
        }
    }

    /**
     * 获取个人信息接口
     *
     * @param request
     * @return
     */
    @Override
    public UserDTO getCurrent(HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserDTO userDTO = new UserDTO();
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(UserConstant.USER_LOGIN_STATE + String.valueOf(user.getId()));
        if (map.size()!=0 && !map.isEmpty()){
            BeanUtil.fillBeanWithMap(map,userDTO,false);
        }else {
            userDTO = setUerDTOAndRedis(user,true);
        }
        return userDTO;
    }

    @Override
    public void loginOut(HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user.getId()==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        stringRedisTemplate.expire(UserConstant.USER_LOGIN_STATE + String.valueOf(user.getId()),3600, TimeUnit.SECONDS);
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerUser(UserDataDTO userDataDTO) {
       if (rateLimiter.tryAcquire()){
           if (StrUtil.isBlank(userDataDTO.getUsername())){
               throw new BusinessException(ErrorCode.NULL_USERACCOUNT);
           }
           if (StrUtil.isBlank(userDataDTO.getUserAccount())){
               throw new BusinessException(ErrorCode.NULL_USERACCOUNT);
           }
           int byUserAccount = userMapper.getByUserAccount(userDataDTO.getUserAccount());
           if (byUserAccount == 1){
               throw new BusinessException(ErrorCode.USER_EXISTS);
           }
           if (StrUtil.isBlank(userDataDTO.getUserPassword()) && userDataDTO.getUserPassword().length() < 6){
               throw new BusinessException(ErrorCode.NULL_PASSWORD);
           }
           if (!userDataDTO.getUserPassword().equals(userDataDTO.getTruePassword())){
               throw new BusinessException(ErrorCode.ALL_PASSWORD);
           }
           QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("userAccount", userDataDTO.getUserAccount());
           User one = getOne(queryWrapper);
           if (one != null){
               throw new BusinessException(ErrorCode.USER_EXISTS);
           }
           String password = DigestUtils.md5DigestAsHex(userDataDTO.getUserPassword().getBytes(StandardCharsets.UTF_8));
           User user = new User();
           BeanUtils.copyProperties(userDataDTO,user);
           user.setUserPassword(password);
           user.setCreateTime(new Date());
           userMapper.insert(user);
       }else {
           throw new BusinessException(ErrorCode.OPERATION_ERROR);
       }
    }

    /**
     * 过滤数据并将数据放入redis
     * @param user
     * @return
     */
    private UserDTO setUerDTOAndRedis(User user,  Boolean b){
        if (user.getId() == null){
            return null;
        }
        User byId =user;
        if (b){
            byId = getById(user.getId());
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(byId.getId());
        userDTO.setUsername(byId.getUsername());
        userDTO.setUserAccount(byId.getUserAccount());
        userDTO.setAvatarUrl(byId.getAvatarUrl());
        userDTO.setGender(byId.getGender());
        userDTO.setPhone(byId.getPhone());
        userDTO.setEmail(byId.getEmail());
        userDTO.setCreateTime(byId.getCreateTime());
        userDTO.setTags(byId.getTags());
        stringRedisTemplate.opsForHash().putAll(UserConstant.USER_LOGIN_STATE+String.valueOf(user.getId()), BeanUtil.beanToMap(userDTO,new HashMap<>(80),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName,fieldValue)-> {
                    if (fieldValue!=null){
                      return fieldValue.toString();
                    }else {
                      return Optional.ofNullable("").get();
                    }
                })));
        return userDTO;
    }
}




