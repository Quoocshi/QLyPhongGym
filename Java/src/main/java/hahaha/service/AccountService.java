package hahaha.service;

import hahaha.model.Account;

public interface AccountService {
    public Long getAccountIdByUsername(String username);
    Account findByEmail(String email);
    Account registerGoogleUser(String email, String name);
}
