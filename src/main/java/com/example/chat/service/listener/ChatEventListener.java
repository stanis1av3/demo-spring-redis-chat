package com.example.chat.service.listener;

import com.example.chat.service.data.MessageService;
import com.example.chat.service.data.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//
//@Service
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChatEventListener {

    @Autowired
    MessageService messageService;
    @Autowired
    RequestService requestService;

    public void onMessagePostedEvent(List<String> recipients) {
        recipients.forEach(userId -> handleQuoteRequests(userId));
    }


    private void handleQuoteRequests(String userId) {
        requestService.getMessageRequests(userId).forEach((e) -> {
            List<String> newQuotes = getLatestMessages(userId);
            e.setResult(newQuotes);
        });
    }


    public List<String> popStackMessages(String userId, DeferredResult<List<String>> result) {

        requestService.getMessageRequests(userId).add(result);
        result.onCompletion(() -> requestService.getMessageRequests(userId).remove(result));
        return getLatestMessages(userId);
    }


    private List<String> getLatestMessages(String userId) {
        //taking all from queue
        List<String> messagesQueue = new CopyOnWriteArrayList<>(messageService.getLatestMessagesQueue(userId));
        //reset queue
        messageService.setLatestMessagesQueue(userId, new ArrayList<>());
        return messagesQueue;
    }


}
