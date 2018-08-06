package com.example.chat.service.data;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RequestService {

    private HashMap<String, Set<DeferredResult<List<String>>>> messageRequests = new HashMap<>();

    public Set<DeferredResult<List<String>>> getMessageRequests(String userId) {
        return messageRequests.computeIfAbsent(userId, k -> new HashSet<>());
    }

}
