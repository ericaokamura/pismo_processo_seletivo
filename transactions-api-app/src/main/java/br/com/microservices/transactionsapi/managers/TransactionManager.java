package br.com.microservices.transactionsapi.managers;

import br.com.microservices.transactionsapi.models.Transaction;
import java.util.List;

public class TransactionManager {

    List<Transaction> transactionList;

    public TransactionManager (List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public boolean isValid (Transaction transaction) {
        for (int i = 0; i < transactionList.size(); i++){
            if (transactionList.get(i).getId() == transaction.getId()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAmountValueValid (Transaction transaction) {
        switch (transaction.getOperation_type()) {
            case PAGAMENTO: if(transaction.getAmount()>=0) return true;
                            break;
            case SAQUE: if(transaction.getAmount()<=0) return true;
                        break;
            case COMPRA_A_VISTA: if(transaction.getAmount()<=0) return true;
                                 break;
            case COMPRA_PARCELADA: if(transaction.getAmount()<=0) return true;
                                   break;
        }
        return false;
    }

    public boolean isBalanceValueValid (Transaction transaction) {
        switch (transaction.getOperation_type()) {
            case PAGAMENTO: if(transaction.getBalance()>=0) return true;
                break;
            case SAQUE: if(transaction.getBalance()<=0) return true;
                break;
            case COMPRA_A_VISTA: if(transaction.getBalance()<=0) return true;
                break;
            case COMPRA_PARCELADA: if(transaction.getBalance()<=0) return true;
                break;
        }
        return false;
    }
}
