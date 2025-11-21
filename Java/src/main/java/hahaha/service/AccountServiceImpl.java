package hahaha.service;

import hahaha.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Override
    public Long getAccountIdByUsername(String username) {
        Long accountId = null;
        accountId = accountRepository.findAccountByUserName(username).getAccountId();
        return accountId;
    }
}
