package com.choyo.msh.payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	public List<Payment> findAccountPayments(Long accountId) {
		return paymentRepository.findByAccountId(accountId);
	}

	public Payment savePayment(Payment payment) {
		return paymentRepository.save(payment);
	}

	public LocalDateTime lastAccountPayment(Long accountId) {
		Payment payment = paymentRepository.findFirstByAccountIdOrderByDateDesc(accountId);
		return Optional.ofNullable(payment).map(p -> p.getDate()).orElse(null);
	}
	
	public Payment findByTransactionId(String transactionId) {
		return paymentRepository.findByTransactionId(transactionId);
	}
}
