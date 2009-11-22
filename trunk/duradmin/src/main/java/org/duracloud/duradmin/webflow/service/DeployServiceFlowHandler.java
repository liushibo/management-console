
package org.duracloud.duradmin.webflow.service;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.duradmin.control.ControllerSupport;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.duracloud.serviceconfig.Deployment;
import org.duracloud.serviceconfig.DeploymentOption;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.Deployment.Status;
import org.duracloud.serviceconfig.DeploymentOption.State;
import org.duracloud.serviceconfig.user.TextUserConfig;
import org.duracloud.serviceconfig.user.UserConfig;
import org.springframework.binding.message.Message;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

public class DeployServiceFlowHandler
        extends AbstractFlowHandler {

    private static final String SUCCESS_OUTCOME = "success";

    private static final String SERVICE = "serviceInfo";
    private static final String DEPLOYMENT = "deployment";

    private static final String SERVICE_ID = "serviceId";

    private static Log log = LogFactory.getLog(DeployServiceFlowHandler.class);

    private ControllerSupport controllerSupport = new ControllerSupport();

    @Override
    public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
        MutableAttributeMap map = super.createExecutionInputMap(request);
        try {
            if (map == null) {
                map = new LocalAttributeMap();
            }

            NavigationUtils.setReturnTo(request, map);

            String serviceIdValue = request.getParameter(SERVICE_ID);
            int serviceId = Integer.valueOf(serviceIdValue);
            ServiceInfo serviceInfo =getService(serviceId);
            map.put(SERVICE, serviceInfo);

            String deploymentIdValue = request.getParameter("deploymentId");
            if(!StringUtils.isBlank(deploymentIdValue)){
                int deploymentId = Integer.valueOf(deploymentIdValue);
                map.put(DEPLOYMENT, getDeployment(serviceInfo, deploymentId));
            }

        } catch (Exception ex) {
            log.error(ex);
        }
        return map;
    }

    private Deployment getDeployment(ServiceInfo serviceInfo, int deploymentId) {
        for(Deployment deployment : serviceInfo.getDeployments()){
            if(deployment.getId() == deploymentId){
                return deployment;
            }
        }
        
        //should never happen
        throw new RuntimeException("deployment id "+ deploymentId + " is not associated with " + serviceInfo);
    }

    private ServiceInfo getService(int serviceId) throws Exception {
        //ServiceInfo s = controllerSupport.getServicesManager().getService(serviceId);
        //return s;
        ServiceInfo service = new ServiceInfo();
        service.setId(serviceId);
        service.setServiceVersion("1.0");
        service.setDisplayName("My Indeterminate Service");
        List<DeploymentOption> dos = new LinkedList<DeploymentOption>();
        DeploymentOption depOption = new DeploymentOption();
        depOption.setDisplayName("Deployment Option xyz");
        depOption.setHostname("127.0.0.1");
        depOption.setState(State.AVAILABLE);
        depOption.setLocation(DeploymentOption.Location.NEW);
        dos.add(depOption);
        service.setDeploymentOptions(dos);
        List<UserConfig> userConfigs = new LinkedList<UserConfig>();
        TextUserConfig config = new TextUserConfig("magicNumber", "Magic Number");
        config.setValue("this is default value");
        userConfigs.add(config);
        
        service.setUserConfigs(userConfigs);

        List<Deployment> deployments = new LinkedList<Deployment>();
        Deployment d = new Deployment();
        d.setId(1);
        d.setStatus(Status.STARTED);

        userConfigs = new LinkedList<UserConfig>();
        config = new TextUserConfig("magicNumber", "Magic Number");
        config.setValue("this is an already configured value");
        userConfigs.add(config);
        d.setUserConfigs(userConfigs);
        deployments.add(d);
        service.setDeployments(deployments);
        
        return service;
    }

    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String returnTo = NavigationUtils.getReturnTo(outcome);
        ServiceInfo service = (ServiceInfo) outcome.getOutput().get(SERVICE);

        String outcomeUrl = null;

        outcomeUrl = ("contextRelative:/services/deployed.htm");

        if (outcome.getId().equals("deployed")) {
            Message message = MessageUtils.createMessage("Successfully deployed service.");
            outcomeUrl =
                    MessageUtils.appendRedirectMessage(outcomeUrl,message,request);
        } else if(outcome.getId().equals("reconfigured")){
            Message message = MessageUtils.createMessage("Successfully reconfigured service.");
            outcomeUrl =
                    MessageUtils.appendRedirectMessage(outcomeUrl,message,request);
            
        } else if (returnTo == null) {
            outcomeUrl = "contextRelative:/services/deployed.htm";
        } else {
            outcomeUrl = returnTo;
        }

        return outcomeUrl;
    }

}
