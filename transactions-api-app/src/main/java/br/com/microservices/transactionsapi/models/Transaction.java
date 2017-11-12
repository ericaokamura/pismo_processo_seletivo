package br.com.microservices.transactionsapi.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import br.com.microservices.transactionsapi.enume.Operations;
import com.fasterxml.jackson.annotation.JsonFormat;

@Component
public class Transaction {
	
	@Id
	private long id;
	
	private long account_id;
	
	private Operations operation_type;
	
	private double amount;
	private double balance;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date eventDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dueDate;

	public Transaction () {

	}
    public Transaction(long id, long account_id, Operations operation_type, double amount, double balance, Date eventDate, Date dueDate) {
    	this.id = id;
    	this.account_id = account_id;
    	this.operation_type = operation_type;
    	this.amount = amount;
    	this.balance = balance;
    	this.eventDate = eventDate;
    	this.dueDate = dueDate;
    }

    public long getId() {
		return id;
	}
	public long getAccount_id() {
		return account_id;
	}
	public void setAccount_id(long account_id) {
		this.account_id = account_id;
	}
	public Operations getOperation_type() {
		return operation_type;
	}
	public void setOperation_type(Operations operation_type) {
		this.operation_type = operation_type;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

}
