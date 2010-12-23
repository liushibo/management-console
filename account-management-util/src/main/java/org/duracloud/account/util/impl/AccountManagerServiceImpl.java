/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */

public class AccountManagerServiceImpl implements AccountManagerService {

    private Logger log =
        LoggerFactory.getLogger(AccountManagerServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    private DuracloudUserService userService;

    public AccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
        DuracloudUserService duracloudUserService) {
        this.repoMgr = duracloudRepoMgr;
        this.userService = duracloudUserService;
    }

    @Override
    public synchronized AccountService createAccount(AccountInfo accountInfo,
                                                     DuracloudUser owner)
        throws SubdomainAlreadyExistsException {
        if (!subdomainAvailable(accountInfo.getSubdomain())) {
            throw new SubdomainAlreadyExistsException();
        }
        try {
            int acctId = getIdUtil().newAccountId();
            AccountInfo newAccountInfo =
                new AccountInfo(acctId,
                                accountInfo.getSubdomain(),
                                accountInfo.getAcctName(),
                                accountInfo.getOrgName(),
                                accountInfo.getDepartment(),
                                accountInfo.getPaymentInfoId(),
                                accountInfo.getInstanceIds(),
                                accountInfo.getStorageProviders());

            getAccountRepo().save(newAccountInfo);

            userService.grantOwnerRights(acctId, owner.getId());
            return new AccountServiceImpl(newAccountInfo, repoMgr);
        } catch (DBConcurrentUpdateException ex) {
            throw new Error(ex);
        }
    }

    @Override
    public AccountService getAccount(int accountId)
        throws AccountNotFoundException {
        try {
            AccountInfo acctInfo = getAccountRepo().findById(accountId);
            return new AccountServiceImpl(acctInfo, repoMgr);

        } catch (DBNotFoundException e) {
            throw new AccountNotFoundException();
        }
    }

    @Override
    public Set<AccountInfo> findAccountsByUserId(int userId) {
        Set<AccountRights> userRights = null;
        Set<AccountInfo> userAccounts = null;
        try {
            userRights = getRightsRepo().findByUserId(userId);
            userAccounts = new HashSet<AccountInfo>();
            for (AccountRights rights : userRights) {
                userAccounts.add(
                    getAccountRepo().findById(rights.getAccountId()));
            }
            return userAccounts;
        } catch (DBNotFoundException e) {
            log.info("No accounts found for user {}", userId);
        }

        return new HashSet<AccountInfo>();

    }

    /*
     * FIXME: This action could be accomplished much more quickly by adding a db
     * query specific to this need, rather than having to loop and check all
     * accounts.
     */
    @Override
    public boolean subdomainAvailable(String subdomain) {
        for (int accountId : getAccountRepo().getIds()) {
            try {
                AccountInfo accountInfo = getAccountRepo().findById(accountId);
                if (accountInfo.getSubdomain().equals(subdomain)) {
                    return false;
                }
            } catch (DBNotFoundException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private DuracloudAccountRepo getAccountRepo() {
        return repoMgr.getAccountRepo();
    }

    private DuracloudRightsRepo getRightsRepo() {
        return repoMgr.getRightsRepo();
    }

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }

}