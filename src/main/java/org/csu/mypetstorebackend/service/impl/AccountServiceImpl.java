package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.csu.mypetstorebackend.entity.Account;
import org.csu.mypetstorebackend.entity.Signon;
import org.csu.mypetstorebackend.persistence.AccountMapper;
import org.csu.mypetstorebackend.persistence.SignonMapper;
import org.csu.mypetstorebackend.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final SignonMapper signonMapper;

    public AccountServiceImpl(AccountMapper accountMapper, SignonMapper signonMapper) {
        this.accountMapper = accountMapper;
        this.signonMapper = signonMapper;
    }

    @Override
    public Account login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String normalizedUsername = username.trim();

        QueryWrapper<Account> accountQuery = new QueryWrapper<>();
        accountQuery.eq("userid", normalizedUsername);
        Account account = accountMapper.selectOne(accountQuery);
        if (account == null) {
            return null;
        }

        QueryWrapper<Signon> signonQuery = new QueryWrapper<>();
        signonQuery.eq("username", normalizedUsername);
        Signon signon = signonMapper.selectOne(signonQuery);

        boolean signonMatches = signon != null && password.equals(signon.getPassword());
        boolean accountMatches = password.equals(account.getPassword());
        if (!signonMatches && !accountMatches) {
            return null;
        }

        return account;
    }

    @Override
    public Account register(Account account) {
        if (account == null || account.getUsername() == null || account.getUsername().trim().isEmpty()
                || account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            return null;
        }
        String normalizedUsername = account.getUsername().trim();

        QueryWrapper<Account> accountQuery = new QueryWrapper<>();
        accountQuery.eq("userid", normalizedUsername);
        if (accountMapper.selectOne(accountQuery) != null) {
            return null;
        }
        QueryWrapper<Signon> signonQuery = new QueryWrapper<>();
        signonQuery.eq("username", normalizedUsername);
        if (signonMapper.selectOne(signonQuery) != null) {
            return null;
        }

        Signon signon = new Signon();
        signon.setUsername(normalizedUsername);
        signon.setPassword(account.getPassword().trim());
        signonMapper.insert(signon);

        account.setUsername(normalizedUsername);
        account.setPassword(account.getPassword().trim());
        account.setStatus("OK");
        account.setMyListOption(1);
        account.setBannerOption(1);

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
        QueryWrapper<Account> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("userid", username);
        accountMapper.update(account, updateWrapper);
        return getAccountByUsername(username);
    }

    @Override
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            return false;
        }
        String normalizedUsername = username.trim();
        String normalizedNewPassword = newPassword.trim();

        Account account = getAccountByUsername(normalizedUsername);
        if (account == null) {
            return false;
        }

        QueryWrapper<Signon> signonQuery = new QueryWrapper<>();
        signonQuery.eq("username", normalizedUsername);
        Signon signon = signonMapper.selectOne(signonQuery);

        boolean signonMatches = signon != null && oldPassword.equals(signon.getPassword());
        boolean accountMatches = oldPassword.equals(account.getPassword());
        if (!signonMatches && !accountMatches) {
            return false;
        }

        if (signon == null) {
            signon = new Signon();
            signon.setUsername(normalizedUsername);
            signon.setPassword(normalizedNewPassword);
            signonMapper.insert(signon);
        } else {
            UpdateWrapper<Signon> signonUpdate = new UpdateWrapper<>();
            signonUpdate.eq("username", normalizedUsername).set("password", normalizedNewPassword);
            signonMapper.update(null, signonUpdate);
        }

        UpdateWrapper<Account> accountUpdate = new UpdateWrapper<>();
        accountUpdate.eq("userid", normalizedUsername).set("password", normalizedNewPassword);
        accountMapper.update(null, accountUpdate);

        Account updatedAccount = getAccountByUsername(normalizedUsername);
        Signon updatedSignon = signonMapper.selectOne(new QueryWrapper<Signon>().eq("username", normalizedUsername));
        return updatedAccount != null
                && updatedSignon != null
                && normalizedNewPassword.equals(updatedAccount.getPassword())
                && normalizedNewPassword.equals(updatedSignon.getPassword());
    }
}
