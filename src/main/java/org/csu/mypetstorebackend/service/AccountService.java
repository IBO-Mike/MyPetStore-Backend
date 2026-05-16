package org.csu.mypetstorebackend.service;

import org.csu.mypetstorebackend.entity.Account;

public interface AccountService {
    Account login(String username, String password);
    Account register(String username, String password);
    Account getAccountByUsername(String username);
    Account updateAccount(String username, Account account);
    boolean changePassword(String username, String oldPassword, String newPassword);
}

