package com.bit.springboot.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友表
 * @TableName friends
 */
@TableName(value ="friends")
public class Friends implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作者
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 好友
     */
    @TableField(value = "friendId")
    private Long friendId;

    /**
     * 好友昵称
     */
    @TableField(value = "friendName")
    private String friendName;

    /**
     * 用户头像
     */
    @TableField(value = "friendAvatarUrl")
    private String friendAvatarUrl;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @TableField(value = "createTime")
    private Date createTime;

    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    public Long getId() {
        return id;
    }

    /**
     * id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 作者
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 作者
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 好友
     */
    public Long getFriendId() {
        return friendId;
    }

    /**
     * 好友
     */
    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    /**
     * 好友昵称
     */
    public String getFriendName() {
        return friendName;
    }

    /**
     * 好友昵称
     */
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    /**
     * 用户头像
     */
    public String getFriendAvatarUrl() {
        return friendAvatarUrl;
    }

    /**
     * 用户头像
     */
    public void setFriendAvatarUrl(String friendAvatarUrl) {
        this.friendAvatarUrl = friendAvatarUrl;
    }
}