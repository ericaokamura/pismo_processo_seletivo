package br.com.microservices.accountsapi.models;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Component
public class Account {

	@Id
	private long id;
	
	private double available_credit_limit;
	private double available_withdrawal_limit;
	
	public Account (long id, double available_credit_limit, double available_withdrawal_limit) {
		super();
		this.id = id;
		this.available_credit_limit = available_credit_limit;
		this.available_withdrawal_limit = available_withdrawal_limit;
	}
	
	public Account() {
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id=id;
	}
	public double getAvailable_credit_limit() {
		return available_credit_limit;
	}
	public void setAvailable_credit_limit(double available_credit_limit) {
		this.available_credit_limit = available_credit_limit;
	}
	public double getAvailable_withdrawal_limit() {
		return available_withdrawal_limit;
	}
	public void setAvailable_withdrawal_limit(double available_withdrawal_limit) {
		this.available_withdrawal_limit = available_withdrawal_limit;
	}


	
	
	
}
