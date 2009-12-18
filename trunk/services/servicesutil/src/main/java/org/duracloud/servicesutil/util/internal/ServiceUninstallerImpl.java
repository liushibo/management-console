package org.duracloud.servicesutil.util.internal;

import org.duracloud.services.common.error.ServiceException;
import org.duracloud.servicesutil.util.ServiceUninstaller;
import org.duracloud.servicesutil.util.catalog.BundleCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Andrew Woods
 */
public class ServiceUninstallerImpl extends ServiceInstallBase
        implements ServiceUninstaller {

    private final Logger log = LoggerFactory.getLogger(getClass());


    public void uninstall(String name) throws Exception {
        log.info("bundleHome: '" + getBundleHome().getBaseDir() + "'");

        if (isJar(name)) {
            uninstallBundleFromHomeAndAttic(name);
        } else if (isZip(name)) {
            uninstallBagAndBundles(name);
        } else {
            throwServiceException("Unsupported filetype: '" + name + "'");
        }
    }

    private void uninstallBundleFromHomeAndAttic(String name)
        throws ServiceException {
        if (BundleCatalog.unRegister(name)) {
            delete(getBundleHome().getContainer(), name);
            delete(getBundleHome().getAttic(), name);
        }
    }

    private void uninstallBagAndBundles(String zipName)
        throws IOException, ServiceException {
        ZipFile zip = new ZipFile(getBundleHome().getFromAttic(zipName));
        Enumeration entries = zip.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String entryName = entry.getName();

            if (isJar(entryName) && BundleCatalog.unRegister(entryName)) {
                delete(getBundleHome().getContainer(), entryName);

            } else if (isWar(entryName)) {                
                String serviceId = FilenameUtils.getBaseName(zipName);
                delete(getBundleHome().getServiceWork(serviceId), entryName);
            }
        }

        delete(getBundleHome().getAttic(), zipName);
    }

    private void delete(File dir, String name) throws ServiceException {
        boolean success = false;
        for (File file : dir.listFiles()) {
            String fileName = file.getName();
            log.debug("found in " + dir.getPath() + ": '" + fileName + "'");

            if (fileName.contains(name)) {
                log.debug("about to delete: " + fileName);
                success = file.delete();
                break;
            }
        }

        if (!success) {
            String msg = "Unable to uninstall service: '" + name + "'";
            log.error(msg);
            super.throwServiceException(msg);
        }
    }

    protected void init() throws Exception {
        // do nothing.
    }
}