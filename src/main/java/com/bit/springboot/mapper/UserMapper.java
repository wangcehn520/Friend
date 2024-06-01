package com.bit.springboot.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bit.springboot.model.dto.UserDTO;
import com.bit.springboot.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author bit
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-05-21 11:53:47
* @Entity generator.domain.User
*/

public interface UserMapper extends BaseMapper<User> {
    /**
     * 修改用户
     * @param
     */
    void updateUser(User user);

    int getByUserAccount(String userAccount);
}




