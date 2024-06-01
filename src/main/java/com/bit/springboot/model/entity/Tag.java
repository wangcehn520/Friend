package com.bit.springboot.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签
 * @TableName tag
 */
@TableName(value ="tag")
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    @TableField(value = "tagName")
    private String tagName;

    /**
     * 用户 id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 父标签 id
     */
    @TableField(value = "parentId")
    private Long parentId;

    /**
     * 0 - 不是, 1 - 父标签
     */
    @TableField(value = "isParent")
    private Integer isParent;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
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
     * 标签名称
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 标签名称
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 用户 id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 用户 id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 父标签 id
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 父标签 id
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 0 - 不是, 1 - 父标签
     */
    public Integer getIsParent() {
        return isParent;
    }

    /**
     * 0 - 不是, 1 - 父标签
     */
    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 是否删除
     */
    public Integer getIsDelete() {
        return isDelete;
    }

    /**
     * 是否删除
     */
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}