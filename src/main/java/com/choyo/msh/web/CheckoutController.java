package com.choyo.msh.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.choyo.msh.account.Account;
import com.choyo.msh.account.AccountService;
import com.choyo.msh.messages.AccountBean;
import com.choyo.msh.payment.Payment;
import com.choyo.msh.payment.PaymentBean;
import com.choyo.msh.payment.PaymentService;

import com.choyo.msh.product.NoProductFoundException;
import com.choyo.msh.product.Product;
import com.choyo.msh.product.ProductService;

@PreAuthorize("hasRole('ROLE_USER')")
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final AccountService accountService;

    private final BraintreeGateway gateway;

    private final ProductService productService;
    
    private final PaymentService paymentService;
    
    private Status[] TRANSACTION_SUCCESS_STATUSES = new Status[]{
            Transaction.Status.AUTHORIZED,
            Transaction.Status.AUTHORIZING,
            Transaction.Status.SETTLED,
            Transaction.Status.SETTLEMENT_CONFIRMED,
            Transaction.Status.SETTLEMENT_PENDING,
            Transaction.Status.SETTLING,
            Transaction.Status.SUBMITTED_FOR_SETTLEMENT
    };

    @Autowired
    public CheckoutController(AccountService accountService, 
    		BraintreeGateway braintreeGatewayBean, 
    		ProductService productService, 
    		PaymentService paymentService) {
        this.accountService = accountService;
        this.gateway = braintreeGatewayBean;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @GetMapping(value = "/")
    public PaymentBean checkout(Principal principal) {
        AccountBean accountBean = new AccountBean(accountService.findAccountByEmail(principal.getName()));
        return PaymentBean.builder()
                .account(accountBean)
                .products(productService.getAllProducts())
                .paymentToken(gateway.clientToken().generate()).build();
    }

    @PostMapping(value = "/")
    public PaymentBean postForm(@RequestParam("code") String code, @RequestParam("payment_method_nonce") String nonce, Principal principal) {
        Account account = accountService.findAccountByEmail(principal.getName());
		AccountBean accountBean = new AccountBean(account);
		
		Product product;

        try {
        	product = productService.findByCode(code);
        } catch (NoProductFoundException e) {
            return PaymentBean.builder()
                    .account(accountBean)
                    .paymentToken(gateway.clientToken().generate())
                    .errors(Collections.singletonList(e.getMessage()))
                    .build();
        }

        TransactionRequest request = new TransactionRequest()
                .amount(product.getPrice())
                .paymentMethodNonce(nonce)
                .customer()
                	.firstName(account.getFirstName())
                	.lastName(account.getLastName())
                	.email(account.getEmail())
                	.done()
                .options()
                	.submitForSettlement(true)
                	.done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            paymentService.savePayment(Payment.builder()
            		.accountId(account.getId())
            		.amount(product.getPrice())
            		.productCode(product.getCode())
            		.transactionId(transaction.getId()).build());
            
            boolean contains = Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus());
            return PaymentBean.builder()
                    .isSuccess(contains)
                    .account(accountBean)
                    .transactionId(transaction.getId())
                    .build();
        } else if (result.getTransaction() != null) {
            Transaction transaction = result.getTransaction();
            
            paymentService.savePayment(Payment.builder()
            		.accountId(account.getId())
            		.amount(product.getPrice())
            		.productCode(product.getCode())
            		.transactionId(transaction.getId()).build());
            
            boolean contains = Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus());
            return PaymentBean.builder()
                    .isSuccess(contains)
                    .account(accountBean)
                    .transactionId(transaction.getId())
                    .build();
        } else {
            List<String> errors = new ArrayList<>();
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
                errors.add(error.getCode() + ":" + error.getMessage());
            }
            return PaymentBean.builder()
                    .isSuccess(false)
                    .account(accountBean)
                    .paymentToken(gateway.clientToken().generate())
                    .errors(errors)
                    .build();
        }
    }


    @GetMapping(value = "/{transactionId}")
    public Payment getTransaction(@PathVariable String transactionId) {
        Transaction transaction = gateway.transaction().find(transactionId);
        //CreditCard creditCard = transaction.getCreditCard();
        //Customer customer = transaction.getCustomer();
            
        Payment payment = paymentService.findByTransactionId(transaction.getId());
        Optional.ofNullable(payment).orElseThrow(()-> new IllegalArgumentException("transaction not found"));
       
        return payment;
    }
}
