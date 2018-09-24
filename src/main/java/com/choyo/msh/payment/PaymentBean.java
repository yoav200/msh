package com.choyo.msh.payment;

import java.util.List;

import com.choyo.msh.messages.AccountBean;
import com.choyo.msh.product.Product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentBean {
	@Builder.Default
	private Boolean isSuccess = Boolean.TRUE;
    private AccountBean account;
    private List<Product> products;
    private String paymentToken;
    private List<String> errors;
    private String transactionId;
}
