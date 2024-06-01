package com.bit.springboot.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.springboot.common.ErrorCode;
import com.bit.springboot.constant.FriendConstant;
import com.bit.springboot.constant.UserConstant;
import com.bit.springboot.exception.BusinessException;
import com.bit.springboot.mapper.FriendsMapper;
import com.bit.springboot.model.entity.Friends;
import com.bit.springboot.model.entity.User;
import com.bit.springboot.model.dto.AddFriendDTO;
import com.bit.springboot.model.vo.FriendDataVO;
import com.bit.springboot.service.FriendsService;
import com.bit.springboot.service.UserService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 86173
* @description 针对表【friends(好友表)】的数据库操作Service实现
* @createDate 2024-05-28 13:38:04
*/
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
    implements FriendsService {
    @Resource
    private UserService userService;
    @Resource
    private FriendsMapper friendsMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addFriend(AddFriendDTO addFriendDTO) {
        Friends friends1 = friendsMapper.searchFriends(addFriendDTO);
        Friends friends2 = friendsMapper.searchFriend(addFriendDTO);
        if (friends1 != null && friends2 !=null){
            throw new BusinessException(ErrorCode.FRIEND_ERROR);
        }
        Long friendId = addFriendDTO.getFriendId();
        User friend = userService.getById(friendId);
        User user = userService.getById(addFriendDTO.getUserId());
        List<Friends> list = new ArrayList<>();
        Friends friends = new Friends();
        friends.setFriendId(friendId);
        friends.setFriendName(friend.getUsername());
        friends.setFriendAvatarUrl(friend.getAvatarUrl());
        friends.setCreateTime(new Date());
        friends.setUserId(addFriendDTO.getUserId());
        list.add(friends);
        Friends friend1 = new Friends();
        friend1.setFriendId(user.getId());
        friend1.setFriendName(user.getUsername());
        friend1.setFriendAvatarUrl(user.getAvatarUrl());
        friend1.setCreateTime(new Date());
        friend1.setUserId(addFriendDTO.getFriendId());
        list.add(friend1);

        FriendsService proxy= (FriendsService) AopContext.currentProxy();
        proxy.saveBatch(list);
        stringRedisTemplate.opsForHash().put(FriendConstant.FRIEND_KEY_POST + friendId, String.valueOf(addFriendDTO.getUserId()), "1");
        stringRedisTemplate.opsForHash().put(FriendConstant.FRIEND_KEY_GET + addFriendDTO.getUserId(), String.valueOf(friendId), "1");

    }

    @Override
    public List<FriendDataVO> searchFriends(Long userId) {
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        List<Friends> friendsList = list(queryWrapper);
        List<FriendDataVO> friendDataVOS = friendsList.stream().map(info -> {
            FriendDataVO friendDataVO = new FriendDataVO();
            BeanUtils.copyProperties(info, friendDataVO);
            return friendDataVO;
        }).collect(Collectors.toList());
        return friendDataVOS;
    }

    @Override
    public List<FriendDataVO> getFriendMessage(HttpServletRequest request) {
        User user =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(FriendConstant.FRIEND_KEY_GET + user.getId());
        Object[] objects = keys.toArray();
        List<FriendDataVO> resultList = new ArrayList<>();
        FriendDataVO friendDataVO = new FriendDataVO();
        for (Object object : objects) {
            String status = (String) stringRedisTemplate.opsForHash().get(FriendConstant.FRIEND_KEY_GET + user.getId(), object);
            String friendId = (String) object;
            System.out.println(friendId);
            Long id = Long.valueOf(friendId);
            User userById = userService.getById(id);
            friendDataVO.setFriendId(userById.getId());
            friendDataVO.setFriendName(userById.getUsername());
            friendDataVO.setFriendAvatarUrl(userById.getAvatarUrl());
            friendDataVO.setStatus(status);
            resultList.add(friendDataVO);
        }
        return resultList;
    }

    @Override
    public List<FriendDataVO> getSendMessage(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(FriendConstant.FRIEND_KEY_POST + user.getId());
        Object[] objects = keys.toArray();
        FriendDataVO friendDataVO = new FriendDataVO();
        List<FriendDataVO> resultList = new ArrayList<>();
        for (Object key : objects) {
            String status =(String) stringRedisTemplate.opsForHash().get(FriendConstant.FRIEND_KEY_POST + user.getId(), key);
            User user1 = userService.getById(Long.valueOf((String) key));
            friendDataVO.setFriendName(user1.getUsername());
            friendDataVO.setFriendId(user1.getId());
            friendDataVO.setFriendAvatarUrl(user1.getAvatarUrl());
            friendDataVO.setStatus(status);
            resultList.add(friendDataVO);
        }
        return resultList;
    }
}




