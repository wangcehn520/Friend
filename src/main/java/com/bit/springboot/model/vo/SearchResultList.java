package com.bit.springboot.model.vo;

import com.bit.springboot.model.entity.User;
import lombok.Data;

import java.util.List;

/**
 * @author LeLe
 * @date 2024/5/31 14:58
 * @Description:
 */
@Data
public class SearchResultList {
    private List<User>  userList;
    private List<PostsDataVO> postsList;
}
