package com.bit.springboot.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bit.springboot.model.entity.Friends;
import com.bit.springboot.model.dto.AddFriendDTO;

import java.util.List;

/**
* @author 86173
* @description 针对表【friends(好友表)】的数据库操作Mapper
* @createDate 2024-05-28 13:38:04
* @Entity generator.domain.Friends
*/
public interface FriendsMapper extends BaseMapper<Friends> {
    List<Friends> searchFriendsList(Long id);

    Friends searchFriends(AddFriendDTO addFriendDTO);

    Friends searchFriend(AddFriendDTO addFriendDTO);

    List<Long> getFriendsId(Long userId);
}




