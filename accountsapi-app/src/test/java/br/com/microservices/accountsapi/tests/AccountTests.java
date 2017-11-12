package br.com.microservices.accountsapi.tests;

import br.com.microservices.accountsapi.managers.AccountManager;
import br.com.microservices.accountsapi.controllers.AccountController;
import br.com.microservices.accountsapi.models.Account;
import br.com.microservices.transactionsapi.models.Payment;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTests {

    MongoClient mongoClient = new MongoClient("localhost", 27017);
    MongoDatabase database = mongoClient.getDatabase("accountdb");
    Morphia morphia = new Morphia();
    Datastore datastore = morphia.createDatastore(mongoClient, "accountdb");

    @Test
    public void deveAdicionarContaSeListaDeContasEstiverVazia () {
        List<Account> list = Collections.emptyList();
        AccountManager amanager = new AccountManager(list);
        Account account = new Account(1, 5000,5000);
        Assert.assertTrue(amanager.isValid(account));
    }

    @Test
    public void garanteQueContaNaoEhExistente() {
        List<Account> list = new ArrayList<>();
        list.add(new Account(1,3000,3000));
        list.add(new Account(2, 3000, 3000));
        AccountManager amanager = new AccountManager(list);
        Account account = new Account(1, 5000,5000);
        Assert.assertFalse(amanager.isValid(account));
    }

    @Test
    public void deveEncontrarPeloId() {
        Account account_saved = new Account(6, 3000,3000);
        datastore.save(account_saved);
        Account account = datastore.createQuery(Account.class).field("id").equal(6).get();
        Assert.assertEquals(6, account.getId());
    }

    @Test
    public void deveDeletarUmaConta() {
        Account account_deleta = new Account (10, 3000,3000);
        datastore.save(account_deleta);
        Query<Account> query = datastore.createQuery(Account.class).field("id").equal(account_deleta.getId());
        datastore.delete(query);
        Account account = datastore.createQuery(Account.class).field("id").equal(10).get();
        Assert.assertNull(account);
    }
}
