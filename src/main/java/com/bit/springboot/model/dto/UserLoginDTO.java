package com.bit.springboot.model.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author LeLe
 * @date 2024/5/22 17:32
 * @Description:
 */
@Data
@ToString
public class UserLoginDTO implements Serializable {
    private String userAccount;
    private String userPassword;
}
