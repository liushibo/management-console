/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import java.util.List;

import org.duracloud.account.db.model.GlobalProperties;
import org.duracloud.account.db.repo.GlobalPropertiesRepo;
import org.duracloud.account.db.util.GlobalPropertiesConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A service for modifying global configuration settings.
 *
 * @author Daniel Bernstein
 */
@Component("globalPropertiesConfigService")
public class GlobalPropertiesConfigServiceImpl implements GlobalPropertiesConfigService {

    @Autowired
    private GlobalPropertiesRepo repo;

    public void setRepo(GlobalPropertiesRepo repo) {
        this.repo = repo;
    }

    public GlobalPropertiesRepo getRepo() {
        return repo;
    }

    @Override
    public GlobalProperties get() {
        List<GlobalProperties> globalProperties = repo.findAll();
        GlobalProperties entity = null;
        if (!globalProperties.isEmpty()) {
            entity = globalProperties.get(0);
        }
        return entity;
    }

    @Override
    public void set(String notifierType,
                    String rabbitmqHost,
                    Integer rabbitmqPort,
                    String rabbitmqVhost,
                    String rabbitmqExchange,
                    String rabbitmqUsername,
                    String rabbitmqPassword,
                    String instanceNotificationTopicArn,
                    String cloudFrontAccountId,
                    String cloudFrontKeyId,
                    String cloudFrontKeyPath) {
        GlobalProperties gp = get();
        if (null == gp) {
            gp = new GlobalProperties();
        }

        gp.setNotifierType(notifierType);
        gp.setRabbitmqHost(rabbitmqHost);
        gp.setRabbitmqPort(rabbitmqPort);
        gp.setRabbitmqVhost(rabbitmqVhost);
        gp.setRabbitmqExchange(rabbitmqExchange);
        gp.setRabbitmqUsername(rabbitmqUsername);
        gp.setRabbitmqPassword(rabbitmqPassword);
        gp.setInstanceNotificationTopicArn(instanceNotificationTopicArn);
        gp.setCloudFrontAccountId(cloudFrontAccountId);
        gp.setCloudFrontKeyId(cloudFrontKeyId);
        gp.setCloudFrontKeyPath(cloudFrontKeyPath);

        repo.save(gp);

    }
}
