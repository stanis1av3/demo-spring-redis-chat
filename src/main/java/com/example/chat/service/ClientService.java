package com.example.chat.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final String USER_ONLINE_KEY = "USER_ONLINE_KEY:";

    public void setOnlineSession(String userId) {
        redisTemplate.opsForValue().set(USER_ONLINE_KEY + userId, LocalDateTime.now().toString(), 90, TimeUnit.SECONDS);
    }

    public Map<String, String> getOnlineSessions() {
        Set<String> userIds = redisTemplate.keys(USER_ONLINE_KEY + "*");

        return Objects.requireNonNull(userIds).stream().collect(Collectors.toMap(t -> t.replace(USER_ONLINE_KEY, Strings.EMPTY),
                        o -> redisTemplate.opsForValue().get(o)));
    }

}
