package com.choyo.msh.payment;

import com.choyo.msh.messages.AccountBean;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PaymentBean {

    public enum PaymentStatus {
        INITIATED, FAIL, SUCCESS
    }

    private PaymentStatus status;
    private AccountBean account;
    private String paymentToken;
    private List<String> errors;
    private String transactionId;
}
