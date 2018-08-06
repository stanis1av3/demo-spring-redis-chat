package com.example.chat.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MessageService {

    @Autowired
    RedisTemplate<String, List<String>> redisTemplate;

    private final static String MESSAGE_QUEUE="MESSAGE_QUEUE:";

    @Transactional
    public void setLatestMessagesQueue(String userId, List<String> queue) {
        redisTemplate.opsForValue().set(MESSAGE_QUEUE+userId, queue, 300, TimeUnit.SECONDS);
    }

    @Transactional
    public List<String> getLatestMessagesQueue(String userId) {
        List<String> messagesQueue = redisTemplate.opsForValue().get(MESSAGE_QUEUE+userId);
        return messagesQueue==null?new ArrayList<>() : messagesQueue;
    }


}
