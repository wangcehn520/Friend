package com.bit.springboot.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.springboot.model.entity.Friends;
import com.bit.springboot.model.dto.AddFriendDTO;
import com.bit.springboot.model.vo.FriendDataVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86173
* @description 针对表【friends(好友表)】的数据库操作Service
* @createDate 2024-05-28 13:38:05
*/
public interface FriendsService extends IService<Friends> {
    /**
     * 添加好友
     * @param addFriendDTO
     */
    void addFriend(AddFriendDTO addFriendDTO);

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    List<FriendDataVO> searchFriends(Long userId);

    /**
     * 获取好友请求信息
     * @param request
     * @return
     */
    List<FriendDataVO> getFriendMessage(HttpServletRequest request);

    /**
     * 获取自己发送请求的信息
     * @param request
     * @return
     */
    List<FriendDataVO> getSendMessage(HttpServletRequest request);
}
