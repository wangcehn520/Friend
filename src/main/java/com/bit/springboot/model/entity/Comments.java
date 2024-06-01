package com.bit.springboot.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bit.springboot.common.PageRequest;
import lombok.Data;

import java.util.Date;


/**
 * @author LeLe
 * @date 2024/5/31 13:11
 * @Description:
 */
@Data
@TableName(value = "comments")
public class Comments {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("postsId")
    private Long postsId;

    @TableField("userId")
    private Long userId;

    @TableField("avatarUrl")
    private String avatarUrl;

    @TableField("author")
    private String author;

    @TableField("comment")
    private String comment;

    @TableField("createTime")
    private Date createTime;

    @TableField("isDelete")
    @TableLogic
    private Integer isDelete;
}