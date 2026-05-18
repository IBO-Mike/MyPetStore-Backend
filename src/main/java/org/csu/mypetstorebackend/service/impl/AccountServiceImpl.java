package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.csu.mypetstorebackend.entity.Account;
import org.csu.mypetstorebackend.entity.Signon;
import org.csu.mypetstorebackend.persistence.AccountMapper;
import org.csu.mypetstorebackend.persistence.SignonMapper;
import org.csu.mypetstorebackend.service.AccountService;
import org.springframework.stereotype.Service;

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
        QueryWrapper<Signon> signonQuery = new QueryWrapper<>();
        signonQuery.eq("username", username);
        Signon signon = signonMapper.selectOne(signonQuery);

        if (signon == null || !signon.getPassword().equals(password)) {
            return null;
        }

        QueryWrapper<Account> accountQuery = new QueryWrapper<>();
        accountQuery.eq("userid", username);
        return accountMapper.selectOne(accountQuery);
    }

    @Override
    public Account register(Account account) {
        if (account == null || account.getUsername() == null || account.getUsername().trim().isEmpty()
                || account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            return null;
        }
        String normalizedUsername = account.getUsername().trim();

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
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            return false;
        }

        QueryWrapper<Signon> signonQuery = new QueryWrapper<>();
        signonQuery.eq("username", username).eq("password", oldPassword);
        Signon signon = signonMapper.selectOne(signonQuery);
        
        if (signon == null) {
            return false;
        }

        UpdateWrapper<Signon> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", username).set("password", newPassword);
        return signonMapper.update(null, updateWrapper) > 0;
    }
}
