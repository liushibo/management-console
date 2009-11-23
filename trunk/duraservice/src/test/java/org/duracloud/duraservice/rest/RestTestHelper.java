package org.duracloud.duraservice.rest;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duraservice.config.DuraServiceConfig;

/**
 * @author Bill Branan
 */
public class RestTestHelper {

    private static String configFileName = "test-duraservice.properties";

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static String baseUrl;

    private static String host = "http://localhost";

    private static String port;
    private static final String defaultPort = "8080";

    private static String webapp = "duraservice";

    private static String initXml = null;

    public static final String SPACE_ACCESS = "OPEN";

    public static HttpResponse initialize() throws Exception {
        String url = getBaseUrl() + "/services";
        if(initXml == null) {
            initXml = buildTestInitXml();
        }
        return restHelper.post(url, initXml, null);
    }

    public static String getBaseUrl() throws Exception {
        if (baseUrl == null) {
            baseUrl = host + ":" + getPort() + "/" + webapp;
        }
        return baseUrl;
    }

    private static String getPort() throws Exception {
        DuraServiceConfig config = new DuraServiceConfig();
        config.setConfigFileName(configFileName);

        if (port == null) {
            port = config.getPort();
        }

        try { // Ensure the port is a valid port value
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            port = defaultPort;
        }

        return port;
    }

    public static String buildTestInitXml() throws Exception {
        String userStorageHost = "localhost";
        String userStoragePort = getPort();
        String userStorageContext = "durastore";
        String userMsgBrokerURL = "tcp://localhost:61617";

        String serviceStorageHost = "localhost";
        String serviceStoragePort = getPort();
        String serviceStorageContext = "durastore";
        String serviceStorageSpaceId = "duracloud-service-repository";

        String serviceComputeMgrType = "AMAZON_EC2";
        String serviceComputeMgrImage = "1234";
        String serviceComputeMgrUser = "username";
        String serviceComputeMgrPass = "password";

        StringBuilder xml = new StringBuilder();
        xml.append("<servicesConfig>");
          xml.append("<userStorage>");
            xml.append("<host>"+userStorageHost+"</host>");
            xml.append("<port>"+userStoragePort+"</port>");
            xml.append("<context>"+userStorageContext+"</context>");
            xml.append("<msgBrokerUrl>"+userMsgBrokerURL+"</msgBrokerUrl>");
          xml.append("</userStorage>");
          xml.append("<serviceStorage>");
            xml.append("<host>"+serviceStorageHost+"</host>");
            xml.append("<port>"+serviceStoragePort+"</port>");
            xml.append("<context>"+serviceStorageContext+"</context>");
            xml.append("<spaceId>"+serviceStorageSpaceId+"</spaceId>");
          xml.append("</serviceStorage>");
          xml.append("<serviceCompute>");
            xml.append("<type>"+serviceComputeMgrType+"</type>");
            xml.append("<imageId>"+serviceComputeMgrImage+"</imageId>");
            xml.append("<computeProviderCredential>");
              xml.append("<username>"+serviceComputeMgrUser+"</username>");
              xml.append("<password>"+serviceComputeMgrPass+"</password>");
            xml.append("</computeProviderCredential>");
          xml.append("</serviceCompute>");
        xml.append("</servicesConfig>");
        return xml.toString();
    }

}