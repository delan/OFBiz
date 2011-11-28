package org.ofbiz.jcr.api;

import org.ofbiz.jcr.access.JcrRepositoryAccessor;

public abstract class AbstractJcrHelper {

    protected JcrRepositoryAccessor access = null;

    public AbstractJcrHelper (JcrRepositoryAccessor accessor) {
        this.access = accessor;
    }

    /**
     * This will close the connection to the content repository and make sure
     * that all changes a stored successfully.
     */
    public void closeContentSession() {
        access.closeAccess();
        access = null;
    }

    /**
     * Remove the passed node from the content repository.
     *
     * @param contentPath
     */
    public void removeContentObject(String contentPath) {
        access.removeContentObject(contentPath);
    }
}
