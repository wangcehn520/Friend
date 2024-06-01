package com.bit.springboot.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author LeLe
 * @date 2024/5/25 12:14
 * @Description:
 */
@Data
public class UserDataDTO implements Serializable {

    private String username;
    private String userAccount;
    private String userPassword;
    private String avatarUrl;
    private Integer gender;
    private String phone;
    private String email;
    private String truePassword;

}
