package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
    public Account register(Account account) {
        if (account == null || account.getUsername() == null || account.getUsername().trim().isEmpty()
                || account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            return null;
        }
        String normalizedUsername = account.getUsername().trim();

        // Check if username already exists
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", normalizedUsername);
        if (accountMapper.selectOne(queryWrapper) != null) {
            return null; // Username already exists
        }

        account.setUsername(normalizedUsername);
        account.setPassword(account.getPassword().trim());
        account.setStatus("OK");
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
        if (username == null || oldPassword == null || newPassword == null) {
            return false;
        }

        UpdateWrapper<Account> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userid", username)
                .eq("password", oldPassword)
                .set("password", newPassword)
                .set("update_time", getCurrentTimestamp());

        return accountMapper.update(null, updateWrapper) > 0;
    }
}
