package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstorebackend.entity.Account;
import org.csu.mypetstorebackend.persistence.AccountMapper;
import org.csu.mypetstorebackend.service.AccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    @Override
    public Account login(String username, String password) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", username);
        Account account = accountMapper.selectOne(queryWrapper);

        if (account != null && account.getPassword().equals(password)) {
            return account;
        }
        return null;
    }

    @Override
    public Account register(String username, String password) {
        // Check if username already exists
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", username);
        if (accountMapper.selectOne(queryWrapper) != null) {
            return null; // Username already exists
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setStatus("active");
        account.setCreateTime(getCurrentTimestamp());
        account.setUpdateTime(getCurrentTimestamp());

        accountMapper.insert(account);
        return account;
    }

    @Override
    public Account getAccountByUsername(String username) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", username);
        return accountMapper.selectOne(queryWrapper);
    }

    @Override
    public Account updateAccount(String username, Account account) {
        account.setUsername(username);
        account.setUpdateTime(getCurrentTimestamp());
        accountMapper.updateById(account);
        return account;
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Account account = getAccountByUsername(username);
        if (account == null || !account.getPassword().equals(oldPassword)) {
            return false;
        }

        account.setPassword(newPassword);
        account.setUpdateTime(getCurrentTimestamp());
        accountMapper.updateById(account);
        return true;
    }
}

