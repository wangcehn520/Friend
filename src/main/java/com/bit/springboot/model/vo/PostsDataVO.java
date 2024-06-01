package com.bit.springboot.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.bit.springboot.common.BaseResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author LeLe
 * @date 2024/5/25 18:33
 * @Description:
 */
@Data
public class PostsDataVO implements Serializable {
    private Long id;
    private String title;
    private String content;
    private String images;
    private String tags;
    private Long likeNumber;
    private String avatarUrl;
    private Long userId;
    private Date createTime;
    private Boolean isLike;
    private String userName;
}
