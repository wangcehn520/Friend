package com.bit.springboot.myTask;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bit.springboot.model.entity.Posts;
import com.bit.springboot.model.vo.PostsDataVO;
import com.bit.springboot.service.PostsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LeLe
 * @date 2024/6/1 0:57
 * @Description:
 */
@Component
@Slf4j
public class MyScheduledTask {
    @Autowired
    private PostsService postsService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 0 * * ?") // 每隔5秒执行一次
    public void myTask() {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("authority",1);
        queryWrapper.last("ORDER BY RAND()").last("limit 3");
        List<Posts> list = postsService.list(queryWrapper);
        List<PostsDataVO> collect = list.stream().sorted((p1, p2) -> {
            return p2.getCreateTime().compareTo(p1.getCreateTime());
        }).collect(Collectors.toList()).stream().map(posts -> {
            PostsDataVO postsDataVO = new PostsDataVO();
            BeanUtils.copyProperties(posts, postsDataVO);
            stringRedisTemplate.opsForList().leftPush("postsList", JSONUtil.toJsonStr(postsDataVO));
            return postsDataVO;
        }).collect(Collectors.toList());
        log.info("每日推荐：{}",collect);
    }
}
