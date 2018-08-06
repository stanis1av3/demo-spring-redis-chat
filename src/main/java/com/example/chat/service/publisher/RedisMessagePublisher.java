package com.example.chat.service.publisher;

import com.example.chat.service.ClientService;
import com.example.chat.service.MessagePublisher;
import com.example.chat.service.data.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class RedisMessagePublisher implements MessagePublisher {

    @Autowired
    private RedisTemplate<String, List<String>> redisTemplate;
    @Autowired
    private ChannelTopic topic;

    @Autowired
    MessageService messageService;

    @Autowired
    ClientService clientService;



    public void publish(List<String> userIds) {
        redisTemplate.convertAndSend(topic.getTopic(), userIds);
    }

    Executor executor = new SimpleAsyncTaskExecutor();

    @PostConstruct
    void started() {
        //todo: for testing purposes repeatable message ping send
        executor.execute(() -> {
            while (run) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<String> userIds = clientService.getOnlineSessions().keySet()
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());

                userIds.forEach(uid->
                {
                    System.out.println(LocalDateTime.now().toString() + " private ping to: "+uid);
                    List<String> latestMessages = messageService.getLatestMessagesQueue(uid);
                    latestMessages.add(LocalDateTime.now().toString()+" Private ping: ");
                    messageService.setLatestMessagesQueue(uid, latestMessages);
                });

                publish(userIds);

            }
        });
    }

    public static Boolean run = true;
    private static Integer a = 0;

}
