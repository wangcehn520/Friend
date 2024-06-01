package com.bit.springboot.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ResultUtils;
import com.bit.springboot.model.entity.Posts;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.vo.PostsDataVO;
import com.bit.springboot.model.vo.SearchResultList;
import com.bit.springboot.service.PostsService;
import com.bit.springboot.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LeLe
 * @date 2024/5/31 14:48
 * @Description:
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostsService postsService;

    @GetMapping("/list/{searchText}")
    public BaseResponse<SearchResultList> list(@PathVariable("searchText") String searchText){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("userAccount",searchText);
        List<User> listByUserAccount = userService.list(queryWrapper);

        QueryWrapper<Posts> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.like("title",searchText);
        List<Posts> listByTitle = postsService.list(queryWrapper1);
        List<PostsDataVO> postsDataVOS = listByTitle.stream().map(posts -> {
            User user = userService.getById(posts.getUserId());
            PostsDataVO postsDataVO = new PostsDataVO();
            BeanUtils.copyProperties(posts, postsDataVO);
            postsDataVO.setUserName(user.getUsername());
            postsDataVO.setAvatarUrl(user.getAvatarUrl());
            return postsDataVO;
        }).collect(Collectors.toList());
        SearchResultList searchResultList = new SearchResultList();
        searchResultList.setPostsList(postsDataVOS);
        searchResultList.setUserList(listByUserAccount);
        return ResultUtils.success(searchResultList);
    }
}
