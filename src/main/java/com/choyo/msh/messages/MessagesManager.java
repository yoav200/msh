package com.choyo.msh.messages;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class MessagesManager {

    private List<MessageBean> messages = new ArrayList<>();

    public void addMessage(MessageBean message) {
        messages.add(message);
    }


    public List<MessageBean> getAllMessage() {
        List<MessageBean> copy = new ArrayList<>(messages);
        messages.clear();
        return copy;
    }


    @Builder
    @Data
    public static class AccountMessages {
        private AccountBean account;
        private List<MessageBean> messages;
    }
}
