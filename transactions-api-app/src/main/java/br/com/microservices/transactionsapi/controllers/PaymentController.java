package br.com.microservices.transactionsapi.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.com.microservices.transactionsapi.managers.PaymentManager;
import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.microservices.transactionsapi.enume.Operations;
import br.com.microservices.transactionsapi.models.Payment;
import br.com.microservices.transactionsapi.models.Transaction;


@RestController
@RequestMapping("/payments")
public class PaymentController {

    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    MongoDatabase database = mongoClient.getDatabase("db");
    Morphia morphia = new Morphia();
    Datastore datastore = morphia.createDatastore(mongoClient, "db");

    PaymentManager pmanager = new PaymentManager(datastore.createQuery(Payment.class).asList());

    @RequestMapping(method=RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	public List<Payment> getAllPayments() {
        return datastore.find(Payment.class).asList();
    }

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="/{id}", method=RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	public Payment getPaymentById (@PathVariable long id) {
        return datastore.createQuery(Payment.class).field("id").equal(id).get();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/{account_id}", method=RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public Payment getPaymentsByAccountId (@PathVariable long account_id) {
        return datastore.createQuery(Payment.class).field("account_id").equal(account_id).get();
    }

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="/tracking/{account_id}", method=RequestMethod.PATCH, produces= MediaType.APPLICATION_JSON_VALUE)
	public List<Payment> createPaymentsForAccountId (@PathVariable long account_id) {

        double sum_pgtos = 0;
        List<Long> credit = new ArrayList<Long>();
        List<Long> debit_avista = new ArrayList<Long>();
        List<Long> debit_parcelado = new ArrayList<Long>();
        List<Long> debit_saque = new ArrayList<Long>();

        double debit_sum = 0;

        List<Transaction> listBalance = datastore.createQuery(Transaction.class).field("account_id").equal(account_id).asList();
        listBalance.sort(Comparator.comparing(Transaction::getEventDate));

        //verificar quais transações são do tipo PAGAMENTO
        for (int i = 0; i < listBalance.size(); i++) {
            if (listBalance.get(i).getOperation_type() == Operations.PAGAMENTO) {
                credit.add(listBalance.get(i).getId());
                sum_pgtos += listBalance.get(i).getAmount();
            }
        }

        //verificar quais transações são do tipo SAQUE
        for (int i = 0; i < listBalance.size(); i++) {
            if (listBalance.get(i).getOperation_type() == Operations.SAQUE) {
                debit_saque.add(listBalance.get(i).getId());
                debit_sum += listBalance.get(i).getAmount();
            }
        }

        //verificar quais transações são do tipo COMPRA PARCELADA
        for (int i = 0; i < listBalance.size(); i++) {
            if (listBalance.get(i).getOperation_type() == Operations.COMPRA_PARCELADA) {
                debit_parcelado.add(listBalance.get(i).getId());
                debit_sum += listBalance.get(i).getAmount();
            }
        }


        //verificar quais transações são do tipo COMPRA À VISTA
        for (int i = 0; i < listBalance.size(); i++) {
            if (listBalance.get(i).getOperation_type() == Operations.COMPRA_A_VISTA) {
                debit_avista.add(listBalance.get(i).getId());
                debit_sum += listBalance.get(i).getAmount();
            }
        }

        List<Long> credit_transaction_id = new ArrayList<Long>();
        List<Long> debit_transaction_id = new ArrayList<Long>();
        List<Double> amount = new ArrayList<Double>();

        for (int j = 0; j < credit.size(); j++) {
            for (int i = 0; i < debit_saque.size(); i++) {
                if (datastore.createQuery(Transaction.class).field("id").equal(debit_saque.get(i)).get().getBalance() != 0 && datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance() != 0) {
                    if (datastore.createQuery(Transaction.class).field("id").equal(debit_saque.get(i)).get().getBalance() + datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance() < 0) {
                        Query<Transaction> query1 = datastore.createQuery(Transaction.class).field("id").equal(debit_saque.get(i));
                        UpdateOperations<Transaction> update1 = datastore.createUpdateOperations(Transaction.class)
                                .inc("balance", datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance());
                        datastore.update(query1, update1);
                        amount.add(datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance());
                        Query<Transaction> query2 = datastore.createQuery(Transaction.class).field("id").equal(credit.get(j));
                        UpdateOperations<Transaction> update2 = datastore.createUpdateOperations(Transaction.class).set("balance", 0.0);
                        datastore.update(query2, update2);

                    } else {
                        Query<Transaction> query2 = datastore.createQuery(Transaction.class).field("id").equal(credit.get(j));
                        UpdateOperations<Transaction> update2 = datastore.createUpdateOperations(Transaction.class)
                                .inc("balance", datastore.createQuery(Transaction.class).field("id").equal(debit_saque.get(i)).get().getBalance());
                        datastore.update(query2,update2);
                        amount.add(-datastore.createQuery(Transaction.class).field("id").equal(debit_saque.get(i)).get().getBalance());
                        Query<Transaction> query1 = datastore.createQuery(Transaction.class).field("id").equal(debit_saque.get(i));
                        UpdateOperations<Transaction> update1 = datastore.createUpdateOperations(Transaction.class).set("balance", 0.0);
                        datastore.update(query1, update1);

                    }
                    credit_transaction_id.add(datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getId());
                    debit_transaction_id.add(datastore.createQuery(Transaction.class).field("id").equal(debit_saque.get(i)).get().getId());

                }
            }
        }


        for (int j = 0; j < credit.size(); j++) {
            for (int i = 0; i < debit_parcelado.size(); i++) {
                if (datastore.createQuery(Transaction.class).field("id").equal(debit_parcelado.get(i)).get().getBalance() != 0 && datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance() != 0) {
                    if (datastore.createQuery(Transaction.class).field("id").equal(debit_parcelado.get(i)).get().getBalance() + datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance() < 0) {
                        Query<Transaction> query1 = datastore.createQuery(Transaction.class).field("id").equal(debit_parcelado.get(i));
                        UpdateOperations<Transaction> update1 = datastore.createUpdateOperations(Transaction.class)
                                .inc("balance", datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance());
                        datastore.update(query1, update1);
                        amount.add(datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance());
                        Query<Transaction> query2 = datastore.createQuery(Transaction.class).field("id").equal(credit.get(j));
                        UpdateOperations<Transaction> update2 = datastore.createUpdateOperations(Transaction.class).set("balance", 0.0);
                        datastore.update(query2, update2);

                    } else {
                        Query<Transaction> query2 = datastore.createQuery(Transaction.class).field("id").equal(credit.get(j));
                        UpdateOperations<Transaction> update2 = datastore.createUpdateOperations(Transaction.class)
                                .inc("balance", datastore.createQuery(Transaction.class).field("id").equal(debit_parcelado.get(i)).get().getBalance());
                        datastore.update(query2,update2);
                        amount.add(-datastore.createQuery(Transaction.class).field("id").equal(debit_parcelado.get(i)).get().getBalance());
                        Query<Transaction> query1 = datastore.createQuery(Transaction.class).field("id").equal(debit_parcelado.get(i));
                        UpdateOperations<Transaction> update1 = datastore.createUpdateOperations(Transaction.class).set("balance", 0.0);
                        datastore.update(query1, update1);

                    }
                    credit_transaction_id.add(datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getId());
                    debit_transaction_id.add(datastore.createQuery(Transaction.class).field("id").equal(debit_parcelado.get(i)).get().getId());

                }
            }
        }


        for (int j = 0; j < credit.size(); j++) {
            for (int i = 0; i < debit_avista.size(); i++) {
                if (datastore.createQuery(Transaction.class).field("id").equal(debit_avista.get(i)).get().getBalance() != 0 && datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance() != 0) {
                    if (datastore.createQuery(Transaction.class).field("id").equal(debit_avista.get(i)).get().getBalance() + datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance() < 0) {
                        Query<Transaction> query1 = datastore.createQuery(Transaction.class).field("id").equal(debit_avista.get(i));
                        UpdateOperations<Transaction> update1 = datastore.createUpdateOperations(Transaction.class)
                                .inc("balance", datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance());
                        datastore.update(query1, update1);
                        amount.add(datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getBalance());
                        Query<Transaction> query2 = datastore.createQuery(Transaction.class).field("id").equal(credit.get(j));
                        UpdateOperations<Transaction> update2 = datastore.createUpdateOperations(Transaction.class).set("balance", 0.0);
                        datastore.update(query2, update2);

                    } else {
                        Query<Transaction> query2 = datastore.createQuery(Transaction.class).field("id").equal(credit.get(j));
                        UpdateOperations<Transaction> update2 = datastore.createUpdateOperations(Transaction.class)
                                .inc("balance", datastore.createQuery(Transaction.class).field("id").equal(debit_avista.get(i)).get().getBalance());
                        datastore.update(query2,update2);
                        amount.add(-datastore.createQuery(Transaction.class).field("id").equal(debit_avista.get(i)).get().getBalance());
                        Query<Transaction> query1 = datastore.createQuery(Transaction.class).field("id").equal(debit_avista.get(i));
                        UpdateOperations<Transaction> update1 = datastore.createUpdateOperations(Transaction.class).set("balance", 0.0);
                        datastore.update(query1, update1);

                    }
                    credit_transaction_id.add(datastore.createQuery(Transaction.class).field("id").equal(credit.get(j)).get().getId());
                    debit_transaction_id.add(datastore.createQuery(Transaction.class).field("id").equal(debit_avista.get(i)).get().getId());
                }
            }
        }


		double soma = 0;
		for (int k = 0; k < amount.size(); k++) {
			soma += amount.get(k);
			Payment payment = new Payment();
			payment.setId(k+1);
			payment.setAccount_id(account_id);
			payment.setCredit_transaction_id(credit_transaction_id.get(k));
			payment.setDebit_transaction_id(debit_transaction_id.get(k));
			payment.setAmount(amount.get(k));
			if(pmanager.isValid(payment)){
                datastore.save(payment);

            }
		}
		return datastore.createQuery(Payment.class).field("account_id").equal(account_id).asList();
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String createPayment(@RequestBody Payment payment) {
        datastore.save(payment);
        return "Pagamento salvo com sucesso!";
	}
	
	@RequestMapping(method=RequestMethod.PATCH, value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String updatePayment(@PathVariable long id, @RequestBody Payment payment) {
        Query<Payment> query = datastore.createQuery(Payment.class)
                .field("id").equal(id);
        UpdateOperations<Payment> update1= datastore.createUpdateOperations(Payment.class)
                .set("credit_transaction_id", payment.getCredit_transaction_id());
        UpdateOperations<Payment> update2= datastore.createUpdateOperations(Payment.class)
                .set("debit_transaction_id", payment.getDebit_transaction_id());
        datastore.update(query, update1);
        datastore.update(query, update2);
        return "Pagamento alterado com sucesso!";
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public String deletePayment(@PathVariable long id) {
        Query<Payment> query = datastore.createQuery(Payment.class).field("id").equal(id);
        datastore.delete(query);
        return "Pagamento deletado com sucesso!";
	}
}
