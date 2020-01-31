/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.account.config.AmaEndpoint;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.AccountServiceFactory;
import org.duracloud.account.db.util.EmailTemplateService;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.account.db.util.security.AnnotationParser;
import org.duracloud.account.db.util.security.SecurityContextUtil;
import org.duracloud.common.changenotifier.AccountChangeNotifier;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.error.NoUserLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * This class creates security-wrapped instances of AccountService.
 *
 * @author Andrew Woods
 * Date: 4/7/11
 */
@Component("accountServiceFactory")
public class AccountServiceFactoryImpl implements AccountServiceFactory {

    private Logger log = LoggerFactory.getLogger(AccountServiceFactoryImpl.class);

    private DuracloudRepoMgr repoMgr;
    private AccessDecisionVoter voter;
    private SecurityContextUtil securityContext;
    private AnnotationParser annotationParser;
    private AmaEndpoint amaEndpoint;
    private AccountChangeNotifier accountChangeNotifier;
    private NotificationMgr notificationMgr;
    private EmailTemplateService emailTemplateService;

    @Autowired
    public AccountServiceFactoryImpl(DuracloudRepoMgr repoMgr,
                                     @Qualifier("acctVoter") AccessDecisionVoter voter,
                                     SecurityContextUtil securityContext,
                                     AnnotationParser annotationParser,
                                     AmaEndpoint amaEndpoint,
                                     AccountChangeNotifier accountChangeNotifier,
                                     NotificationMgr notificationMgr,
                                     EmailTemplateService emailTemplateService) {
        this.repoMgr = repoMgr;
        this.voter = voter;
        this.securityContext = securityContext;
        this.annotationParser = annotationParser;
        this.amaEndpoint = amaEndpoint;
        this.accountChangeNotifier = accountChangeNotifier;
        this.notificationMgr = notificationMgr;
        this.emailTemplateService = emailTemplateService;
    }

    @Override
    public AccountService getAccount(Long acctId)
        throws AccountNotFoundException {
        AccountInfo acctInfo = repoMgr.getAccountRepo().findOne(acctId);
        return getAccount(acctInfo);
    }

    @Override
    public AccountService getAccount(AccountInfo acctInfo) {
        AccountService acctService = new AccountServiceImpl(amaEndpoint,
                                                            acctInfo,
                                                            repoMgr,
                                                            accountChangeNotifier,
                                                            notificationMgr,
                                                            emailTemplateService);

        Authentication authentication = getAuthentication();
        return new AccountServiceSecuredImpl(acctService,
                                             authentication,
                                             voter,
                                             annotationParser);
    }

    private Authentication getAuthentication() {
        try {
            return securityContext.getAuthentication();

        } catch (NoUserLoggedInException e) {
            log.warn("No user found in security context.");
            throw new DuraCloudRuntimeException(e);
        }
    }
}
