
package org.duracloud.duradmin.cache;

import junit.framework.TestCase;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.duradmin.mock.contentstore.MockContentStoreManagerFactoryImpl;
import org.junit.Assert;

public class TestContentStoreCache
        extends TestCase {

    private ContentStoreCache cache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cache = new ContentStoreCache();
        ContentStoreManager manager =
                new MockContentStoreManagerFactoryImpl().create();
        cache.setContentStore(manager.getPrimaryContentStore());

    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testGetContentStoreCache() {
        Assert.assertNotNull(cache.getContentStore());
    }

    public void testGetSpaces() throws Exception {
        Assert.assertNotNull(cache.getSpaces());
    }
}