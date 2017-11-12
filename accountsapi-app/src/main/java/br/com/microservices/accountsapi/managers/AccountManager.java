package br.com.microservices.accountsapi.managers;

import br.com.microservices.accountsapi.models.Account;

import java.util.List;

public class AccountManager {

    private List<Account> accountList;

    public AccountManager (List<Account> accountList) {
        this.accountList = accountList;
    }

    public boolean isValid (Account account) {
        for (int i = 0; i < accountList.size(); i++){
            if (accountList.get(i).getId() == account.getId()) {
                return false;
            }
        }
        return true;
    }
}
