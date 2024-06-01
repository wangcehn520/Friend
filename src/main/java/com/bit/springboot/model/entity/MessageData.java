package com.bit.springboot.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LeLe
 * @date 2024/5/30 1:43
 * @Description:
 */
@Data
public class MessageData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fromUserId;
    private String toUserId;
    private String content;
    private String type;
    private String author;
    private String avatarUrl;

}
