package br.com.microservices.transactionsapi.tests;

import br.com.microservices.transactionsapi.enume.Operations;
import br.com.microservices.transactionsapi.managers.PaymentManager;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentTests {

    MongoClient mongoClient = new MongoClient("localhost", 27017);
    MongoDatabase database = mongoClient.getDatabase("db");
    Morphia morphia = new Morphia();
    Datastore datastore = morphia.createDatastore(mongoClient, "db");

    @Test
    public void deveAdicionarPagamentoSeListaDePagamentosEstiverVazia(){
        List<Payment> list = Collections.emptyList();
        PaymentManager pmanager = new PaymentManager(list);
        Payment payment = new Payment(1,1,2000,2000);
        Assert.assertTrue(pmanager.isValid(payment));
    }

    @Test
    public void deveEncontrarPeloId() {
        Payment payment_saved = new Payment(3,1,3000,3000);
        datastore.save(payment_saved);
        Payment payment = datastore.createQuery(Payment.class).field("id").equal(3).get();
        Assert.assertEquals(3, payment.getId());
    }

    @Test
    public void deveDeletarUmPagamento() {
        Payment payment_deleta = new Payment (10, 1,3000,3000);
        datastore.save(payment_deleta);
        Query<Payment> query = datastore.createQuery(Payment.class).field("id").equal(payment_deleta.getId());
        datastore.delete(query);
        Payment payment = datastore.createQuery(Payment.class).field("id").equal(10).get();
        Assert.assertNull(payment);
    }
}
