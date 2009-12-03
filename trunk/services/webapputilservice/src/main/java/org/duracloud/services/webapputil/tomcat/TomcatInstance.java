package org.duracloud.services.webapputil.tomcat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duracloud.common.util.IOUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.services.webapputil.error.WebAppDeployerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * This class is a stateful representation of a tomcat instance.
 * It may be started or stopped,
 * and
 * webapps may be deployed to it or undeployed from it.
 *
 * @author Andrew Woods
 *         Date: Dec 2, 2009
 */
public class TomcatInstance {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private File catalinaHome;
    private int port;

    public TomcatInstance(File catalinaHome, int port) {
        this.catalinaHome = catalinaHome;
        this.port = port;
    }

    /**
     * This method starts the tomcat instance.
     * i.e. ./bin/startup.[sh|bat]
     */
    public void start() {
        runScript(getStartUpScript());
        waitForStartup();
    }

    /**
     * This  method shutsdown the tomcat instance.
     * i.e. ./bin/shutdown.[sh|bat]
     */
    public void stop() {
        runScript(getShutdownScript());
        waitForShutdown();
    }

    private void waitForStartup() {
        isRunning(true);
    }

    private void waitForShutdown() {
        isRunning(false);
    }

    private void isRunning(boolean state) {
        int tries = 0;
        int maxTries = 20;
        while (isRunning() != state && tries++ < maxTries) {
            sleep(500);
        }

        if (isRunning() != state) {
            String verb = state ? "start" : "stop";
            throw new WebAppDeployerException("Unable to " + verb + " tomcat");
        }
    }

    private boolean isRunning() {
        boolean running = false;

        RestHttpHelper httpHelper = new RestHttpHelper();
        RestHttpHelper.HttpResponse response = null;
        try {
            response = httpHelper.get("http://localhost:" + port);
        } catch (Exception e) {
            // do nothing.
        }

        if (response != null && response.getStatusCode() == 200) {
            running = true;
        }

        return running;
    }

    private void runScript(File script) {
        String CATALINA = "CATALINA_HOME=" + catalinaHome.getAbsolutePath();
        String JAVA = "JAVA_HOME=" + System.getProperty("java.home");
        String[] env = {CATALINA, JAVA};

        Process proc = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            proc = runtime.exec(script.getAbsolutePath(), env);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error running script: \n -- ");
            sb.append(script.getAbsolutePath());
            sb.append("\n -- catalinaHome: ");
            sb.append(catalinaHome.getAbsolutePath());
            sb.append("\n -- " + e.getMessage());
            log.error(sb.toString());
            throw new WebAppDeployerException(sb.toString(), e);
        }

        if (proc != null) {
            logMessages(proc.getErrorStream());
            logMessages(proc.getInputStream());
        }
    }

    private void logMessages(InputStream input) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = readLine(reader)) != null) {
            sb.append(line + "\n\t");
        }
        if (sb.length() > 0) {
            log.warn(sb.toString());
        }
        IOUtils.closeQuietly(reader);
    }

    private String readLine(BufferedReader reader) {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            // do nothing.
        }
        return line;
    }

    private File getStartUpScript() {
        String os = System.getProperty("os.name");
        String filename = "startup.sh";
        if (os != null && os.toLowerCase().contains("windows")) {
            filename = "startup.bat";
        }
        return getScript(filename);
    }

    private File getShutdownScript() {
        String os = System.getProperty("os.name");
        String filename = "shutdown.sh";
        if (os != null && os.toLowerCase().contains("windows")) {
            filename = "shutdown.bat";
        }
        return getScript(filename);
    }

    private File getScript(String filename) {
        File script = new File(getBinDir(), filename);
        if (!script.exists()) {
            String msg = "script not found:" + script.getAbsolutePath();
            log.error(msg);
            throw new WebAppDeployerException(msg);
        }
        return script;
    }

    private File getBinDir() {
        return new File(catalinaHome, "bin");
    }

    /**
     * This method deploys the arg war into the appserver under the arg context
     *
     * @param context of deployed webapp
     * @param war     to be deployed
     */
    public void deploy(String context, InputStream war) {
        File webappsDir = new File(catalinaHome, "webapps");
        File warFile = new File(webappsDir, context + ".war");
        OutputStream output = IOUtil.getOutputStream(warFile);
        IOUtil.copy(war, output);

        IOUtils.closeQuietly(war);
        IOUtils.closeQuietly(output);
    }

    /**
     * This method undeploys the webapp found under the arg context
     *
     * @param context to be undeployed
     */
    public void unDeploy(String context) {
        File webappsDir = new File(catalinaHome, "webapps");
        File warFile = new File(webappsDir, context + ".war");
        File warDir = new File(webappsDir, context);

        FileUtils.deleteQuietly(warFile);
        try {
            FileUtils.deleteDirectory(warDir);
        } catch (IOException e) {
            log.warn("Error deleting warDir: " + warDir.getAbsolutePath(), e);
        }
    }

    public File getCatalinaHome() {
        return catalinaHome;
    }

    public int getPort() {
        return port;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing.
        }
    }

}