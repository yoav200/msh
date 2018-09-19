package com.choyo.msh.messages;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageBean {

    private final long timestamp = System.currentTimeMillis();

    private MessageType type;

    private String message;

    public enum MessageType {
        SIGIN, PURCHASE, ERROR, INFO, SUCCESS
    }
}
