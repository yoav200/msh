package com.choyo.msh.messages;

import java.util.ArrayList;
import java.util.List;

public class MessagesManager {
    private List<MessageBean> messages = new ArrayList<>();

    void addMessage(MessageBean message) {
        messages.add(message);
    }

    public MessageBean getLastMessage() {
        MessageBean messageBean = null;
        int size = messages.size();
        if (size > 0) {
            int index = size - 1;
            messageBean = messages.get(index);
            messages.remove(index);
        }
        return messageBean;
    }

    public List<MessageBean> getAllMessage() {
        List<MessageBean> copy = new ArrayList<MessageBean>(messages);
        messages.clear();
        return copy;
    }
}
