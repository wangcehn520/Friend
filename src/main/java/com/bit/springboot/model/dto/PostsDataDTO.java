package com.bit.springboot.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LeLe
 * @date 2024/5/25 14:35
 * @Description:
 */
@Data
public class PostsDataDTO implements Serializable {
    private String title;
    private String content;
    private String images;
    /**
     * 1为公开 2为仅好友可见
     */
    private Integer authority;
    private String tags;
}
