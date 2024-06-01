package com.bit.springboot.server;


import cn.hutool.json.JSONUtil;
import com.bit.springboot.constant.MessageConstant;
import com.bit.springboot.model.entity.MessageData;
import com.bit.springboot.utils.GenerateUUIDUtils;
import org.apache.poi.xwpf.usermodel.TOC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author LeLe
 * @date 2024/5/29 17:55
 * @Description:
 */
@Component
@ServerEndpoint(value = "/ws/{sid}")
public class ChatMessageServer {

    public static RedisTemplate<String,String> redisTemplate;
    //存放会话对象
    private static Map<String, Session> sessionMap = new HashMap();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        System.out.println("客户端：" + sid + "建立连接");
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        MessageData messageData = JSONUtil.toBean(message, MessageData.class);
        privateSendMessage(messageData);

        System.out.println("收到来自客户端：" + sid + "的信息:" + message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("连接断开:" + sid);
        sessionMap.remove(sid);
    }

    /**
     * 群发
     *
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 私聊
     */
    public void privateSendMessage(MessageData messageData){
        String toUserId = messageData.getToUserId();
        String fromUserId = messageData.getFromUserId();
        String uuid = GenerateUUIDUtils.generateUniqueId(Long.valueOf(toUserId), Long.valueOf(fromUserId));
        Session session = sessionMap.get(toUserId);
        if (session == null){
            System.out.println("用户不在线");
            redisTemplate.opsForList().rightPush(MessageConstant.PRIVATE_MESSAGE_KEY+uuid,JSONUtil.toJsonStr(messageData));
            redisTemplate.expire(MessageConstant.PRIVATE_MESSAGE_KEY+uuid, Duration.ofDays(7));
            if (redisTemplate.opsForHash().entries(MessageConstant.RECENTLY_CONTACTED_KEY+fromUserId).isEmpty()){
                Map<String,String> map =new HashMap<>();
                Map<String,String> map1 =new HashMap<>();
                map1.put("friendId",toUserId);
                map1.put("friendName",messageData.getAuthor());
                map1.put("friendAvatarUrl",messageData.getAvatarUrl());
                String jsonStr = JSONUtil.toJsonStr(map1);
                map.put(toUserId,jsonStr);
                redisTemplate.opsForHash().putAll(MessageConstant.RECENTLY_CONTACTED_KEY+fromUserId,map);
            }
        }else {
            try {
                session.getBasicRemote().sendText(String.valueOf(messageData));
            } catch (Exception e){
                e.getMessage();
            }
        }
    }
    @Autowired
    public void setRedisTemplate(RedisTemplate<String,String> redisTemplate){
        ChatMessageServer.redisTemplate = redisTemplate;
    }
}
