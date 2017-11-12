package br.com.microservices.transactionsapi.models;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Component
public class Payment {
	
	@Id
	private long id;
	private long account_id;
	private long credit_transaction_id;
	private long debit_transaction_id;
	private double amount;

	public Payment (long id, long account_id, long credit_transaction_id, long debit_transaction_id) {
		this.id =id;
		this.account_id=account_id;
		this.credit_transaction_id=credit_transaction_id;
		this.debit_transaction_id=debit_transaction_id;
	}

	public Payment() {

	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getAccount_id() {
		return account_id;
	}
	public void setAccount_id(long account_id) {
		this.account_id = account_id;
	}
	public long getCredit_transaction_id() {
		return credit_transaction_id;
	}
	public void setCredit_transaction_id(long credit_transaction_id) {
		this.credit_transaction_id = credit_transaction_id;
	}
	public long getDebit_transaction_id() {
		return debit_transaction_id;
	}
	public void setDebit_transaction_id(long debit_transaction_id) {
		this.debit_transaction_id = debit_transaction_id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	

}
