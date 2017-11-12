package br.com.microservices.accountsapi.controllers;

import java.util.ArrayList;
import java.util.List;
import br.com.microservices.accountsapi.managers.AccountManager;
import br.com.microservices.transactionsapi.enume.Operations;
import br.com.microservices.transactionsapi.models.Payment;
import br.com.microservices.transactionsapi.models.Transaction;
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
import br.com.microservices.accountsapi.models.Account;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    MongoClient mongoClient = new MongoClient("localhost", 27017);
    MongoDatabase database = mongoClient.getDatabase("accountdb");
    MongoDatabase database2 = mongoClient.getDatabase("db");
    Morphia morphia = new Morphia();
    Datastore datastore = morphia.createDatastore(mongoClient, "accountdb");
    Datastore datastore2 = morphia.createDatastore(mongoClient, "db");

    AccountManager manager = new AccountManager(datastore.createQuery(Account.class).asList());

    @ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="", method=RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	public List<Account> getAllAccounts() {
        return this.datastore.createQuery(Account.class).asList();
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="/{id}", method=RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	public Account getAccount(@PathVariable long id) {
        return this.datastore.createQuery(Account.class).field("id").equal(id).get();
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String createAccount(@RequestBody Account account) {
        if(manager.isValid(account)) {
            this.datastore.save(account);
            return "Conta salva com sucesso!";
        } else return "ID de conta já existente";
    }

	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public String deleteAccount(@PathVariable long id) {
        Query<Account> query = datastore.createQuery(Account.class).field("id").equal(id);
        datastore.delete(query);
        return "Conta deletada com sucesso!";
	}

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateAccount(@PathVariable long id) {

        double debit_sum = 0;
        double sum_pgto = 0;

        List<Payment> listPayments = new ArrayList<Payment>();
        listPayments = datastore2.createQuery(Payment.class).field("account_id").equal(id).asList();
        List<Transaction> listTransactions = new ArrayList<Transaction>();
        listTransactions = datastore2.createQuery(Transaction.class).field("account_id").equal(id).asList();

        for (int i = 0; i < listPayments.size(); i++) {
            sum_pgto += listPayments.get(i).getAmount();
        }

        //verificar quais transações são do tipo SAQUE
        for (int i = 0; i < listTransactions.size(); i++) {
            if (listTransactions.get(i).getOperation_type() == Operations.SAQUE) {
                debit_sum += listTransactions.get(i).getAmount();
            }
        }

        //verificar quais transações são do tipo COMPRA PARCELADA
        for (int i = 0; i < listTransactions.size(); i++) {
            if (listTransactions.get(i).getOperation_type() == Operations.COMPRA_PARCELADA) {
                debit_sum += listTransactions.get(i).getAmount();
            }
        }

        //verificar quais transações são do tipo COMPRA À VISTA
        for (int i = 0; i < listTransactions.size(); i++) {
            if (listTransactions.get(i).getOperation_type() == Operations.COMPRA_A_VISTA) {
                debit_sum += listTransactions.get(i).getAmount();
            }
        }

        Query<Account> query = datastore.createQuery(Account.class).field("id").equal(id);
        UpdateOperations<Account> update1 = datastore.createUpdateOperations(Account.class)
                .inc("available_credit_limit", sum_pgto + debit_sum);
        UpdateOperations<Account> update2 = datastore.createUpdateOperations(Account.class)
                .inc("available_withdrawal_limit", sum_pgto + debit_sum);
        datastore.update(query, update1);
        datastore.update(query, update2);
        return "Conta atualizada com sucesso!";
    }
}
