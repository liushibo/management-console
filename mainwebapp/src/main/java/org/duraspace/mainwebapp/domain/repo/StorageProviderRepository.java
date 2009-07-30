
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.StorageProvider;
import org.duraspace.storage.domain.StorageProviderType;

public interface StorageProviderRepository {

    /**
     * {@inheritDoc}
     */
    public abstract StorageProvider findStorageProviderById(int id)
            throws Exception;

    /**
     * {@inheritDoc}
     */
    public abstract List<Integer> getStorageProviderIds() throws Exception;

    public abstract int saveStorageProvider(StorageProvider storageProvider)
            throws Exception;

    public abstract int findStorageProviderIdByProviderType(StorageProviderType providerType)
            throws Exception;

}