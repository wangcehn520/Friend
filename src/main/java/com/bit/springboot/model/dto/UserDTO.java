package com.bit.springboot.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author LeLe
 * @date 2024/5/22 21:38
 * @Description:
 */
@Data
public class UserDTO implements Serializable {


    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */

    private String avatarUrl;

    /**
     * 性别
     */

    private Integer gender;



    /**
     * 电话
     */

    private String phone;

    /**
     * 邮箱
     */

    private String email;



    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 标签 json 列表
     */

    private String tags;
    /**
     * 是否为好友
     */
    private Boolean isFriend;
    /**
     * 是否是本人
     */
    private Boolean isMe;


}
