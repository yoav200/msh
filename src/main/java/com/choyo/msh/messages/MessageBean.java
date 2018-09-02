package com.choyo.msh.messages;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageBean {
    private String state; // error, info, success
    private String message;
}
