package com.choyo.msh.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByAccountId(Long accountId);
	
	Payment findFirstByAccountIdOrderByDateDesc(Long accountId);
	
	Payment findByTransactionId(String transactionId);
}
