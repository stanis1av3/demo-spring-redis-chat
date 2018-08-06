package com.example.chat.service.listener;

import com.example.chat.service.listener.ChatEventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;


public class RedisMessageSubscriber implements MessageListener {

    public RedisMessageSubscriber(ChatEventListener chatEventListener) {
        this.chatEventListener = chatEventListener;
    }

    private ChatEventListener chatEventListener;

    public void onMessage(Message message, byte[] pattern) {
        try {
            chatEventListener.onMessagePostedEvent( (List<String>)new ObjectInputStream( new ByteArrayInputStream(message.getBody())).readObject() );
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
