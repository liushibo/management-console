/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.core;

import java.util.Date;

public abstract class Resource {

    private final Path path;

    private final Date modifiedDate;

    private final boolean isCollection;

    protected Resource(Path path,
                       Date modifiedDate,
                       boolean isCollection) {
        this.path = path;
        this.modifiedDate = modifiedDate;
        this.isCollection = isCollection;
    }

    public Path getPath() {
        return path;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public boolean isCollection() {
        return isCollection;
    }

}
