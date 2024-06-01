package com.bit.springboot.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LeLe
 * @date 2024/5/25 14:29
 * @Description:
 */
@TableName("posts")
@Data
public class Posts implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "title")
    private String title;

    @TableField(value = "content")
    private String content;

    @TableField(value = "images")
    private String images;

    @TableField(value = "authority")
    private Integer authority;

    @TableField(value = "tags")
    private String tags;

    @TableField(value = "userId")
    private Long userId;

    @TableField(value = "createTime")
    private Date createTime;

    @TableField(value = "updateTime")
    private Date updateTime;

    @TableField(value = "likeNumber")
    private Long likeNumber;

    @TableField(value = "avatarUrl")
    private String avatarUrl;

    @TableField(value = "userName")
    private String userName;

    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;
}
