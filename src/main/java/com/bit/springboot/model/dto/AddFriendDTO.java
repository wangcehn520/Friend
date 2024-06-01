package com.bit.springboot.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LeLe
 * @date 2024/5/28 13:31
 * @Description:
 */
@Data
public class AddFriendDTO implements Serializable {
    private Long userId;
    private Long friendId;
}
