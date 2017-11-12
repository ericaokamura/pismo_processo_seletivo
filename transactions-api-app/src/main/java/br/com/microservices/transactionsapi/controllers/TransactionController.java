package br.com.microservices.transactionsapi.controllers;

import br.com.microservices.transactionsapi.managers.TransactionManager;
import br.com.microservices.transactionsapi.models.Transaction;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import java.util.List;
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


@RestController
@RequestMapping("/transactions")
public class TransactionController {

    MongoClient mongoClient = new MongoClient("localhost", 27017);
    MongoDatabase database = mongoClient.getDatabase("db");
    Morphia morphia = new Morphia();
    Datastore datastore = morphia.createDatastore(mongoClient, "db");

    TransactionManager tmanager = new TransactionManager(datastore.createQuery(Transaction.class).asList());

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transaction> getAllTransactions() {
        return datastore.find(Transaction.class).asList();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Transaction getTransactionById(@PathVariable long id) {
        return datastore.createQuery(Transaction.class).field("id").equal(id).get();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/usuarios/{account_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transaction> getTransactionsByAccountId(@PathVariable long account_id) {
        return datastore.createQuery(Transaction.class).field("account_id").equal(account_id).asList();
    }
	
	@RequestMapping(method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String createTransaction(@RequestBody Transaction transaction) {
        if(tmanager.isValid(transaction)) {
            if(tmanager.isAmountValueValid(transaction) && tmanager.isBalanceValueValid(transaction)){
                datastore.save(transaction);
                return "Transação salva com sucesso!";
            }
            else return "Valores inválidos de montante e/ou de balanço ou operação inválida";
        }
        else return "ID de transação já existente";
	}

    @RequestMapping(method=RequestMethod.PATCH, value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String updateTransaction(@PathVariable long id, @RequestBody Transaction transaction) {
        Query<Transaction> query = datastore.createQuery(Transaction.class)
                .field("id").equal(id);
        UpdateOperations<Transaction> update1= datastore.createUpdateOperations(Transaction.class)
                .set("account_id", transaction.getAccount_id());
        UpdateOperations<Transaction> update2= datastore.createUpdateOperations(Transaction.class)
                .set("operation_type", transaction.getOperation_type());
        UpdateOperations<Transaction> update3= datastore.createUpdateOperations(Transaction.class)
                .set("amount", transaction.getAmount());
        UpdateOperations<Transaction> update4= datastore.createUpdateOperations(Transaction.class)
                .set("balance", transaction.getBalance());
        UpdateOperations<Transaction> update5= datastore.createUpdateOperations(Transaction.class)
                .set("eventDate", transaction.getEventDate());
        UpdateOperations<Transaction> update6= datastore.createUpdateOperations(Transaction.class)
                .set("dueDate", transaction.getDueDate());

        datastore.update(query, update1);
        datastore.update(query, update2);
        datastore.update(query, update3);
        datastore.update(query, update4);
        datastore.update(query, update5);
        datastore.update(query, update6);

        return "Transação alterada com sucesso!";
    }

	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public String deleteTransaction(@PathVariable long id) {
        Query<Transaction> query = datastore.createQuery(Transaction.class).field("id").equal(id);
        datastore.delete(query);
        return "Transação deletada com sucesso!";
    }
}
