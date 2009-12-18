package org.duracloud.durastore.rest;

import junit.framework.TestCase;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Runtime test of space REST API. The durastore
 * web application must be deployed and available at the
 * baseUrl location in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestSpaceRest
        extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl;

    private static String spaceId;

    static {
        String random = String.valueOf(new Random().nextInt(99999));
        spaceId = "space" + random;
    }

    @Override
    @Before
    protected void setUp() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();

        // Initialize the Instance
        HttpResponse response = RestTestHelper.initialize();
        assertEquals(200, response.getStatusCode());

        // Add space
        response = RestTestHelper.addSpace(spaceId);
        assertEquals(201, response.getStatusCode());
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        HttpResponse response =
            RestTestHelper.deleteSpace(spaceId);

        assertEquals(200, response.getStatusCode());
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains(spaceId));
        assertTrue(responseText.contains("deleted"));
    }

    @Test
    public void testGetSpaces() throws Exception {
        String url = baseUrl + "/spaces";
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();

        assertEquals(200, response.getStatusCode());
        assertTrue(responseText.contains("<spaces>"));
    }

    @Test
    public void testGetSpaceMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId;
        HttpResponse response = restHelper.head(url);
        assertEquals(200, response.getStatusCode());

        testMetadata(response,
                     BaseRest.SPACE_ACCESS_HEADER,
                     RestTestHelper.SPACE_ACCESS);
        testMetadata(response,
                     RestTestHelper.METADATA_NAME,
                     RestTestHelper.METADATA_VALUE);
    }

    @Test
    public void testGetSpaceContents() throws Exception {
        String url = baseUrl + "/" + spaceId;
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();

        assertEquals(200, response.getStatusCode());
        assertTrue(responseText.contains("<space"));
    }

    @Test
    public void testUpdateSpaceMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId;

        // Add metadata
        Map<String, String> headers = new HashMap<String, String>();
        String newSpaceAccess = "CLOSED";
        headers.put(BaseRest.SPACE_ACCESS_HEADER, newSpaceAccess);
        String newSpaceMetadata = "Updated Space Metadata";
        headers.put(RestTestHelper.METADATA_NAME, newSpaceMetadata);
        HttpResponse response = restHelper.post(url, null, headers);

        assertEquals(200, response.getStatusCode());
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains(spaceId));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        response = restHelper.head(url);
        assertEquals(200, response.getStatusCode());

        testMetadata(response, BaseRest.SPACE_ACCESS_HEADER, newSpaceAccess);
        testMetadata(response, RestTestHelper.METADATA_NAME, newSpaceMetadata);

        // Remove metadata
        headers.remove(RestTestHelper.METADATA_NAME);
        response = restHelper.post(url, null, headers);

        assertEquals(200, response.getStatusCode());
        responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains(spaceId));
        assertTrue(responseText.contains("updated"));

        response = restHelper.head(url);
        assertEquals(200, response.getStatusCode());

        testNoMetadata(response, RestTestHelper.METADATA_NAME);        
    }

    private void testMetadata(HttpResponse response, String name, String value)
            throws Exception {
        String metadata = response.getResponseHeader(name).getValue();
        assertNotNull(metadata);
        assertEquals(metadata, value);
    }

    private void testNoMetadata(HttpResponse response,
                                String name) throws Exception {
        assertNull(response.getResponseHeader(name));
    }

 }