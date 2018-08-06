package com.example.chat.web;

import com.example.chat.service.listener.ChatEventListener;
import com.example.chat.service.ChatService;
import com.example.chat.service.ClientService;
import com.example.chat.transport.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;

@Controller
public class ChatController {

    @Autowired
    ChatEventListener chatEventListener;

    @Autowired
    ChatService chatService;

    @Autowired
    ClientService clientService;

    @RequestMapping(value =  "/quotes",
            method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<String>> quotesGet(@RequestParam(required = true) String userId) {

        clientService.setOnlineSession(userId);

        final DeferredResult<List<String>> result = new DeferredResult<List<String>>(20000l, Collections.emptyList());
        List<String> list = chatEventListener.popStackMessages(userId, result);
        if (list.size() > 0) {
            result.setResult(list);
        }
        return result;
    }

    @RequestMapping(value = "/quotes",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity quotesPost(@RequestBody MessageDTO messageDTO) {
        chatService.postMessage(messageDTO);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value =  "/quotes/clients",
            method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> quotesGetClients(@RequestParam(required = false) List<String> userId) {
        return clientService.getOnlineSessions();
    }

}
