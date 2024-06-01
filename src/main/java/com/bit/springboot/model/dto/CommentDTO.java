package com.bit.springboot.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LeLe
 * @date 2024/5/31 13:21
 * @Description:
 */
@Data
public class CommentDTO implements Serializable {
    private Long postsId;
    private Long userId;
    private String avatarUrl;
    private String author;
    private String comment;

}
