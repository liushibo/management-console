/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.duracloud.account.db.model.DuracloudMill;
import org.duracloud.account.db.util.DuracloudMillConfigService;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.binding.message.Message;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Daniel Bernstein
 */
@RunWith(EasyMockRunner.class)
public class DuracloudMillControllerTest extends EasyMockSupport {

    @Mock
    private DuracloudMillConfigService service;

    @TestSubject
    private DuracloudMillController controller = new DuracloudMillController();

    String dbHost = "host";
    Integer dbPort = 100;
    String dbName = "name";
    String dbUsername = "user";
    String dbPassword = "password";
    String auditQueue = "auditQueue";
    String auditLogSpaceId = "auditLogSpaceId";
    String auditQueueTyope = "AWS";
    String auditQueueHost = "N/A";

    @After
    public void tearDown() {
        verifyAll();
    }

    @Test
    public void testGet() {
        replayAll();
        assertNotNull(this.controller.get());
    }

    @Test
    public void testForm() {

        DuracloudMill mill = createMock(DuracloudMill.class);
        expect(mill.getDbHost()).andReturn(dbHost);
        expect(mill.getDbPort()).andReturn(dbPort);
        expect(mill.getDbName()).andReturn(dbName);
        expect(mill.getDbUsername()).andReturn(dbUsername);
        expect(mill.getDbPassword()).andReturn(dbPassword);
        expect(mill.getAuditQueue()).andReturn(auditQueue);
        expect(mill.getAuditLogSpaceId()).andReturn(auditLogSpaceId);
        expect(mill.getAuditQueueType()).andReturn(auditQueueTyope);
        expect(mill.getAuditQueueHost()).andReturn(auditQueueHost);

        expect(this.service.get()).andReturn(mill);
        replayAll();
        DuracloudMillForm form = this.controller.form();
        assertNotNull(form);
        assertEquals(form.getDbHost(), dbHost);
        assertEquals(form.getDbPort(), dbPort);
        assertEquals(form.getDbName(), dbName);
        assertEquals(form.getDbUsername(), dbUsername);
        assertEquals(form.getDbPassword(), dbPassword);
        assertEquals(form.getAuditQueue(), auditQueue);
        assertEquals(form.getAuditLogSpaceId(), auditLogSpaceId);
        assertEquals(form.getAuditQueueType(), auditQueueTyope);
        assertEquals(form.getAuditQueueHost(), auditQueueHost);

    }

    @Test
    public void testEdit() {
        replayAll();
        assertNotNull(this.controller.edit());
    }

    @Test
    public void testUpdate() {
        DuracloudMillForm form = createMock(DuracloudMillForm.class);
        BindingResult result = createMock(BindingResult.class);
        Model model = createMock(Model.class);
        RedirectAttributes redirectAttributes = createMock(RedirectAttributes.class);

        expect(result.hasErrors()).andReturn(false);

        expect(form.getDbHost()).andReturn(dbHost);
        expect(form.getDbPort()).andReturn(dbPort);
        expect(form.getDbName()).andReturn(dbName);
        expect(form.getDbUsername()).andReturn(dbUsername);
        expect(form.getDbPassword()).andReturn(dbPassword);
        expect(form.getAuditQueue()).andReturn(auditQueue);
        expect(form.getAuditLogSpaceId()).andReturn(auditLogSpaceId);
        this.service.set(dbHost, dbPort, dbName, dbUsername, dbPassword,
                         auditQueue, auditLogSpaceId);
        expectLastCall();

        expect(
            redirectAttributes.addFlashAttribute(isA(String.class),
                                                 isA(Message.class))).andReturn(redirectAttributes);
        expectLastCall();
        replayAll();
        this.controller.update(form, result, model, redirectAttributes);
    }

}
