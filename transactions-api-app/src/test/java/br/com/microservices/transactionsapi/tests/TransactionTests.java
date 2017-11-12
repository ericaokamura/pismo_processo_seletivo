package br.com.microservices.transactionsapi.tests;

import br.com.microservices.transactionsapi.controllers.TransactionController;
import br.com.microservices.transactionsapi.enume.Operations;
import br.com.microservices.transactionsapi.managers.TransactionManager;
import br.com.microservices.transactionsapi.models.Payment;
import br.com.microservices.transactionsapi.models.Transaction;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongojack.Aggregation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionTests {

    MongoClient mongoClient = new MongoClient("localhost", 27017);
    MongoDatabase database = mongoClient.getDatabase("db");
    Morphia morphia = new Morphia();
    Datastore datastore = morphia.createDatastore(mongoClient, "db");

    @Test
    public void deveAdicionarTransacaoSeListaDeTransacoesEstiverVazia () {
        List<Transaction> list = Collections.emptyList();
        TransactionManager tmanager = new TransactionManager(list);
        Transaction transaction = new Transaction(1,1, Operations.PAGAMENTO,300,300, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(),new GregorianCalendar(2017, Calendar.OCTOBER, 05).getTime());
        Assert.assertTrue(tmanager.isValid(transaction));
    }

    @Test
    public void garanteQueTransacaoNaoEhExistente() {
        List<Transaction> list = new ArrayList<>();
        list.add(new Transaction(1, 1, Operations.SAQUE, -300.0, -300.0, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(), new GregorianCalendar(2017, Calendar.OCTOBER, 05).getTime()));
        list.add(new Transaction(2, 1, Operations.COMPRA_A_VISTA, -300.0, -300.0, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(), new GregorianCalendar(2017, Calendar.OCTOBER, 05).getTime()));
        TransactionManager tmanager = new TransactionManager(list);
        Transaction transaction = new Transaction(1, 1, Operations.SAQUE, -300.0, -300.0, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(), new GregorianCalendar(2017, Calendar.OCTOBER, 05).getTime());
        Assert.assertFalse(tmanager.isValid(transaction));
    }

    @Test
    public void garanteQueOValorMontanteSejaValido() {
        List<Transaction> list = new ArrayList<>();
        Transaction transaction = new Transaction(1,1, Operations.COMPRA_A_VISTA, 3000,-3000, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(), new GregorianCalendar(2017, Calendar.OCTOBER, 05).getTime());
        list.add(transaction);
        TransactionManager tmanager = new TransactionManager(list);
        Assert.assertFalse(tmanager.isAmountValueValid(transaction));
    }

    @Test
    public void garanteQueOValorDeBalancoSejaValido() {
        List<Transaction> list = new ArrayList<>();
        Transaction transaction = new Transaction(1, 1, Operations.PAGAMENTO, 200,-200, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(), new GregorianCalendar(2017, Calendar.OCTOBER, 05).getTime());
        list.add(transaction);
        TransactionManager tmanager = new TransactionManager(list);
        Assert.assertFalse(tmanager.isBalanceValueValid(transaction));
    }

    @Test
    public void deveEncontrarPeloId() {
        Transaction transaction_saved = new Transaction(100,1,Operations.COMPRA_A_VISTA, -3000,-3000, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(), new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime());
        datastore.save(transaction_saved);
        Transaction transaction = datastore.createQuery(Transaction.class).field("id").equal(100).get();
        Assert.assertEquals(100, transaction.getId());
    }

    @Test
    public void deveDeletarUmaTransacao() {
        Transaction transaction_deleta = new Transaction(10,1, Operations.COMPRA_A_VISTA, -500,-500, new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime(), new GregorianCalendar(2017, Calendar.SEPTEMBER, 07).getTime());
        datastore.save(transaction_deleta);
        Query<Transaction> query = datastore.createQuery(Transaction.class).field("id").equal(transaction_deleta.getId());
        datastore.delete(query);
        Transaction transaction = datastore.createQuery(Transaction.class).field("id").equal(10).get();
        Assert.assertNull(transaction);
    }
}
