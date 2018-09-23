package com.choyo.msh.web;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.choyo.msh.account.AccountService;
import com.choyo.msh.messages.AccountBean;
import com.choyo.msh.payment.PaymentBean;

@PreAuthorize("hasRole('ROLE_USER')")
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final AccountService accountService;

    private final BraintreeGateway gateway;

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
    public CheckoutController(AccountService accountService, BraintreeGateway braintreeGatewayBean) {
        this.accountService = accountService;
        this.gateway = braintreeGatewayBean;
    }

    @GetMapping(value = "/")
    public PaymentBean checkout(Principal principal) {
        AccountBean accountBean = new AccountBean(accountService.findAccountByEmail(principal.getName()));
        return PaymentBean.builder()
                .account(accountBean)
                .paymentToken(gateway.clientToken().generate()).build();
    }

    @PostMapping(value = "/")
    public PaymentBean postForm(@RequestParam("amount") String amount, @RequestParam("payment_method_nonce") String nonce, Principal principal) {
        AccountBean accountBean = new AccountBean(accountService.findAccountByEmail(principal.getName()));
        BigDecimal decimalAmount;

        try {
            decimalAmount = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            return PaymentBean.builder()
                    .status(PaymentBean.PaymentStatus.INITIATED)
                    .account(accountBean)
                    .paymentToken(gateway.clientToken().generate())
                    .errors(Collections.singletonList("Error: 81503: Amount is an invalid format."))
                    .build();
        }

        TransactionRequest request = new TransactionRequest()
                .amount(decimalAmount)
                .paymentMethodNonce(nonce)
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            boolean contains = Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus());
            return PaymentBean.builder()
                    .status(contains ? PaymentBean.PaymentStatus.SUCCESS : PaymentBean.PaymentStatus.FAIL)
                    .account(accountBean)
                    .transactionId(transaction.getId())
                    .build();
        } else if (result.getTransaction() != null) {
            Transaction transaction = result.getTransaction();
            boolean contains = Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus());
            return PaymentBean.builder()
                    .status(contains ? PaymentBean.PaymentStatus.SUCCESS : PaymentBean.PaymentStatus.FAIL)
                    .account(accountBean)
                    .transactionId(transaction.getId())
                    .build();
        } else {
            List<String> errors = new ArrayList<>();
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
                errors.add(error.getCode() + ":" + error.getMessage());
            }
            return PaymentBean.builder()
                    .status(PaymentBean.PaymentStatus.FAIL)
                    .account(accountBean)
                    .paymentToken(gateway.clientToken().generate())
                    .errors(errors)
                    .build();
        }
    }


    @RequestMapping(value = "/checkouts/{transactionId}")
    public String getTransaction(@PathVariable String transactionId, Model model) {
        Transaction transaction;
        CreditCard creditCard;
        Customer customer;

        try {
            transaction = gateway.transaction().find(transactionId);
            creditCard = transaction.getCreditCard();
            customer = transaction.getCustomer();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return "redirect:/checkouts";
        }

        model.addAttribute("isSuccess", Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus()));
        model.addAttribute("transaction", transaction);
        model.addAttribute("creditCard", creditCard);
        model.addAttribute("customer", customer);

        return "checkouts/show";
    }
}
