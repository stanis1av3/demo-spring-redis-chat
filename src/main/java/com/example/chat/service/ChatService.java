package com.example.chat.service;

import com.example.chat.service.data.MessageService;
import com.example.chat.service.publisher.RedisMessagePublisher;
import com.example.chat.transport.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    RedisMessagePublisher redisMessagePublisher;

    @Autowired
    MessageService messageService;

    @Autowired
    ClientService clientService;

    public void postMessage(MessageDTO messageDTO) {

        Set<String> userIds = getOnlineClientsList(messageDTO);
        pushMessagesToClientsStacks(messageDTO, userIds);
        publishEventToClients(messageDTO);

    }

    private void publishEventToClients(MessageDTO messageDTO) {
        redisMessagePublisher.publish(messageDTO.getTo());
    }

    private Set<String> getOnlineClientsList(MessageDTO messageDTO) {
        return clientService.getOnlineSessions().keySet().stream()
                    .filter(s -> messageDTO.getTo().contains(s))
                    .collect(Collectors.toSet());
    }

    private void pushMessagesToClientsStacks(MessageDTO messageDTO, Set<String> userIds) {
        userIds.forEach(uid -> {
                            List<String> messagesQueue = messageService.getLatestMessagesQueue(uid);
                            messagesQueue.add(messageDTO.getText());
                            messageService.setLatestMessagesQueue(uid, messagesQueue);
                        }
                );
    }
}
