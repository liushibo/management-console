/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.webflow.content;

import java.io.IOException;

import org.duracloud.duradmin.contentstore.ContentStoreProviderTestBase;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.FileDataTest;
import org.duracloud.mock.MockMessageContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.message.MessageContext;
import org.springframework.web.multipart.MultipartFile;

public class AddContentItemActionTest
        extends ContentStoreProviderTestBase {

    private AddContentItemAction addContentItemAction;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.addContentItemAction = new AddContentItemAction();
        this.addContentItemAction.setContentStoreProvider(contentStoreProvider);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetContentStoreProvider() throws Exception {
        Assert.assertNotNull(this.addContentItemAction
                .getContentStoreProvider().getContentStore());
    }

    private MultipartFile createFile() throws IOException {
        return FileDataTest.createTestFile("test.jpg", "image/jpg", "test".getBytes());

    }
    @Test
    public void testExecuteSuccess() throws Exception {
        ContentItem contentItem = new ContentItem();
        MultipartFile file = createFile();

        
        String spaceId =
                this.contentStoreProvider.getContentStore().getSpaces().get(0);
        Space space = new Space();
        space.setSpaceId(spaceId);

        contentItem.setSpaceId(space.getSpaceId());
        contentItem.setFile(file);
        MessageContext messageContext = new MockMessageContext();
        Assert.assertTrue(this.addContentItemAction.execute(contentItem,
                                                            space,
                                                            messageContext));
    }

    @Test
    public void testExecuteFailure() throws Exception {
        ContentItem contentItem = new ContentItem();
        MultipartFile file = createFile();

        String spaceId = "randomspaceid";
        Space space = new Space();
        space.setSpaceId(spaceId);

        contentItem.setSpaceId(spaceId);
        contentItem.setFile(file);
        MessageContext messageContext = new MockMessageContext();
        Assert.assertFalse(this.addContentItemAction.execute(contentItem,
                                                             space,
                                                             messageContext));

    }

}