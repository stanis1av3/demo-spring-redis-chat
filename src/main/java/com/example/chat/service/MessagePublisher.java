package com.example.chat.service;

import java.util.List;

public interface MessagePublisher {
    void publish(List<String> recipients);
}