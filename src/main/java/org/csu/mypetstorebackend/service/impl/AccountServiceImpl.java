package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstorebackend.entity.Account;
import org.csu.mypetstorebackend.persistence.AccountMapper;
import org.csu.mypetstorebackend.service.AccountService;
import org.csu.mypetstorebackend.utils.TimeUtil;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    private String getCurrentTimestamp() {
        return TimeUtil.currentMysqlDateTime();
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
        String normalizedUsername = username == null ? null : username.trim();
        if (normalizedUsername == null || normalizedUsername.isEmpty()
                || password == null || password.trim().isEmpty()) {
            return null;
        }

        // Check if username already exists
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", normalizedUsername);
        if (accountMapper.selectOne(queryWrapper) != null) {
            return null; // Username already exists
        }

        Account account = new Account();
        account.setUsername(normalizedUsername);
        account.setPassword(password);
        account.setStatus("OK");
        account.setEmail("");
        account.setFirstName("");
        account.setLastName("");
        account.setAddress1("");
        account.setAddress2("");
        account.setCity("");
        account.setState("");
        account.setZip("");
        account.setCountry("");
        account.setPhone("");
        account.setLanguagePrefer("English");
        account.setFavoriteCategory("DOGS");
        account.setMyListOption(1);
        account.setBannerOption(1);
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
