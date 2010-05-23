package org.duracloud.services.j2kservice.osgi;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import static org.ops4j.pax.exam.CoreOptions.mavenConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.profile;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

/**
 * @author Andrew Woods
 *         Date: Dec 20, 2009
 */
@RunWith(JUnit4TestRunner.class)
public class AbstractDuracloudOSGiTestBasePax {

    @Inject
    private BundleContext bundleContext;

    protected static final String BASE_DIR_PROP = "base.dir";
    protected static final String BUNDLE_HOME_PROP = "BUNDLE_HOME";

    @Before
    public void setUp() throws Exception {
        Thread.sleep(2000);
    }

    @Configuration
    public static Option[] configuration() {

        Option bundles = bundle("file:target/j2kservice-1.0.0.jar");

        Option frameworks = CoreOptions.frameworks(CoreOptions.equinox(),
                                                   // Knops does not work for this service.
                                                   // CoreOptions.knopflerfish(),
                                                   CoreOptions.felix());

        return options(bundles,
                       mavenConfiguration(),
                       systemProperties(),
                       frameworks,
                       profile("spring.dm"));
    }

    private static Option systemProperties() {
        String baseDir = System.getProperty(BASE_DIR_PROP);
        Assert.assertNotNull(baseDir);

        String bundleDir = System.getProperty(BUNDLE_HOME_PROP);
        Assert.assertNotNull(bundleDir);

        return CoreOptions.systemProperties(CoreOptions.systemProperty(
            BASE_DIR_PROP).value(baseDir), CoreOptions.systemProperty(
            BUNDLE_HOME_PROP).value(bundleDir));
    }

    protected BundleContext getBundleContext() {
        Assert.assertNotNull(bundleContext);
        return bundleContext;
    }

}