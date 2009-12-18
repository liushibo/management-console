
package org.duracloud.storage.provider;

import java.io.InputStream;

import java.util.Iterator;
import java.util.Map;

import org.duracloud.common.util.metrics.Metric;
import org.duracloud.common.util.metrics.MetricException;
import org.duracloud.common.util.metrics.MetricsProbed;
import org.duracloud.common.util.metrics.MetricsTable;
import org.duracloud.storage.error.StorageException;

/**
 * This class wraps a StorageProvider implementation, collecting timing metrics
 * while passing calls down.
 *
 * @author Andrew Woods
 */
public abstract class ProbedStorageProvider
        implements StorageProvider, MetricsProbed {

    protected StorageProvider storageProvider;

    protected MetricsTable metricsTable;

    protected Metric metric;

    abstract protected MetricsProbed getProbedCore();

    protected void startMetric(String methodName) {
        if (metric == null) {
            metric = new Metric(getClass().getName(), methodName);
            getMetricsTable().addMetric(metric);
        } else {
            metric.addElement(methodName);
        }

        MetricsTable subTable = new MetricsTable();
        this.metricsTable.addSubMetric(metric, subTable);
        MetricsProbed probed = getProbedCore();
        probed.setMetricsTable(subTable);

        metric.start(methodName);
    }

    protected void stopMetric(String methodName) {
        metric.stop(methodName);
    }

    public void setMetricsTable(MetricsTable metricsTable) {
        this.metricsTable = metricsTable;
        this.metric = null;
    }

    private MetricsTable getMetricsTable() {
        if (this.metricsTable == null) {
            throw new RuntimeException(new MetricException("Metrics table has not been set."));
        }
        return this.metricsTable;
    }

    protected void setStorageProvider(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    public String addContent(String spaceId,
                             String contentId,
                             String contentMimeType,
                             long contentSize,
                             InputStream content) throws StorageException {
        startMetric("addContent");
        String result =
                storageProvider.addContent(spaceId,
                                           contentId,
                                           contentMimeType,
                                           contentSize,
                                           content);
        stopMetric("addContent");
        return result;
    }

    public void createSpace(String spaceId) throws StorageException {
        startMetric("createSpace");
        storageProvider.createSpace(spaceId);
        stopMetric("createSpace");
    }

    public void deleteContent(String spaceId, String contentId)
            throws StorageException {
        startMetric("deleteContent");
        storageProvider.deleteContent(spaceId, contentId);
        stopMetric("deleteContent");
    }

    public void deleteSpace(String spaceId) throws StorageException {
        startMetric("deleteSpace");
        storageProvider.deleteSpace(spaceId);
        stopMetric("deleteSpace");
    }

    public InputStream getContent(String spaceId, String contentId)
            throws StorageException {
        startMetric("getContent");
        InputStream result = storageProvider.getContent(spaceId, contentId);
        stopMetric("getContent");
        return result;
    }

    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws StorageException {
        startMetric("getContentMetadata");
        Map<String, String> result =
                storageProvider.getContentMetadata(spaceId, contentId);
        stopMetric("getContentMetadata");
        return result;
    }

    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        startMetric("getSpaceAccess");
        AccessType result = storageProvider.getSpaceAccess(spaceId);
        stopMetric("getSpaceAccess");
        return result;
    }

    public Iterator<String> getSpaceContents(String spaceId)
            throws StorageException {
        startMetric("getSpaceContents");
        Iterator<String> result = storageProvider.getSpaceContents(spaceId);
        stopMetric("getSpaceContents");
        return result;
    }

    public Map<String, String> getSpaceMetadata(String spaceId)
            throws StorageException {
        startMetric("getSpaceMetadata");
        Map<String, String> result = storageProvider.getSpaceMetadata(spaceId);
        stopMetric("getSpaceMetadata");
        return result;
    }

    public Iterator<String> getSpaces() throws StorageException {
        startMetric("getSpaces");
        Iterator<String> result = storageProvider.getSpaces();
        stopMetric("getSpaces");
        return result;
    }

    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws StorageException {
        startMetric("setContentMetadata");
        storageProvider.setContentMetadata(spaceId, contentId, contentMetadata);
        stopMetric("setContentMetadata");
    }

    public void setSpaceAccess(String spaceId, AccessType access)
            throws StorageException {
        startMetric("setSpaceAccess");
        storageProvider.setSpaceAccess(spaceId, access);
        stopMetric("setSpaceAccess");
    }

    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
            throws StorageException {
        startMetric("setSpaceMetadata");
        storageProvider.setSpaceMetadata(spaceId, spaceMetadata);
        stopMetric("setSpaceMetadata");
    }

}