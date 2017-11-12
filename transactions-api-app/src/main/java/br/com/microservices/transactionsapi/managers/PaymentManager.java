package br.com.microservices.transactionsapi.managers;

import br.com.microservices.transactionsapi.models.Payment;
import java.util.List;

public class PaymentManager {

    List<Payment> paymentsList;

    public PaymentManager (List<Payment> paymentsList) {
        this.paymentsList = paymentsList;
    }

    public boolean isValid (Payment payment) {
        for (int i = 0; i < paymentsList.size(); i++){
            if (paymentsList.get(i).getId() == payment.getId()) {
                return false;
            }
        }
        return true;
    }

}
