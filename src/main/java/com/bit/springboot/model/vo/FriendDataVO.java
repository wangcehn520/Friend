package com.bit.springboot.model.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * @author LeLe
 * @date 2024/5/28 13:46
 * @Description:
 */
@Data
public class FriendDataVO implements Serializable {
    private Long friendId;
    private String friendName;
    private String friendAvatarUrl;
    private String status;
}
