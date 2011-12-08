package org.ofbiz.jcr.access.jackrabbit;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.ocm.exception.ObjectContentManagerException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.jcr.access.ContentWriter;
import org.ofbiz.jcr.access.VersioningManager;
import org.ofbiz.jcr.orm.OfbizRepositoryMapping;
import org.ofbiz.jcr.util.jackrabbit.JcrUtilJackrabbit;

public class ContentWriterJackrabbit implements ContentWriter {

    private static String module = ContentWriterJackrabbit.class.getName();

    private ObjectContentManager ocm = null;
    VersioningManager versioningManager = null;

    /**
     *
     * @param ocm
     */
    public ContentWriterJackrabbit(ObjectContentManager ocm) {
        this.ocm = ocm;
        versioningManager = new VersioningManagerJackrabbit(ocm);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.ofbiz.jcr.access.ContentWriter#storeContentObject(org.ofbiz.jcr.orm
     * .OfbizRepositoryMapping)
     */
    @Override
    public void storeContentObject(OfbizRepositoryMapping orm) throws ObjectContentManagerException, ItemExistsException {
        if (orm == null) {
            return;
        }

        // we want to avoid same name sibling (SnS) for each node Type
        try {
            if (this.ocm.getSession().itemExists(orm.getPath())) {
                // we could either throw an exception or call the update method
                throw new ItemExistsException("There already exists an object stored at " + orm.getPath() + ". Please use update if you want to change it.");
            }
        } catch (ItemExistsException e) {
            throw (e);
        } catch (RepositoryException e) {
            Debug.logError(e, module);
            return;
        }

        // create all nodes in the node structure which do not exist yet
        try {
            createNodeStructure(orm.getPath(), orm.getClass().getAnnotation(org.apache.jackrabbit.ocm.mapper.impl.annotation.Node.class).jcrType());
        } catch (PathNotFoundException e) {
            Debug.logError(e, "The new node could not be created: " + orm.getPath(), module);
            return;
        } catch (RepositoryException e) {
            Debug.logError(e, "The new node could not be created: " + orm.getPath(), module);
            return;
        }

        ocm.insert(orm);
        versioningManager.addContentToCheckInList(orm.getPath());

        this.saveState();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.ofbiz.jcr.access.ContentWriter#updateContentObject(org.ofbiz.jcr.
     * orm.OfbizRepositoryMapping)
     */
    @Override
    public void updateContentObject(OfbizRepositoryMapping orm) throws ObjectContentManagerException {
        versioningManager.checkOutContentObject(orm.getPath());
        ocm.update(orm);
        this.saveState();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.ofbiz.jcr.access.ContentWriter#removeContentObject(java.lang.String)
     */
    @Override
    public void removeContentObject(String nodePath) throws ObjectContentManagerException {
        nodePath = JcrUtilJackrabbit.createAbsoluteNodePath(nodePath);
        versioningManager.checkOutContentObject(nodePath, true);

        ocm.remove(nodePath);
        this.saveState();
    }

    private void saveState() {
        versioningManager.checkInContentAndSaveState();
    }

    /**
     * Create the new Node structure.
     *
     * @param completeNodePath
     * @param primaryNodeType
     *            a primary node type from the mapping annotation class, that
     *            will be used as primary type for the created nodes
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    private void createNodeStructure(String completeNodePath, String primaryNodeType) throws PathNotFoundException, RepositoryException {
        // We have to check if the node structure (the sub nodes of the passed
        // ORM Object) exist, otherwise they will be created.
        String[] nodeStructure = completeNodePath.split(ConstantsJackrabbit.NODEPATHDELIMITER);
        Node parentNode = null;
        try {
            parentNode = this.ocm.getSession().getRootNode();
        } catch (RepositoryException e) {
            Debug.logError(e, "The new node could not be created: " + completeNodePath, module);
            return;
        }

        // We loop only over the sub nodes.
        for (int i = 0; i < (nodeStructure.length - 1); i++) {
            String node = nodeStructure[i];
            if (UtilValidate.isEmpty(node)) {
                continue;
            }

            parentNode = createNewSubNodeIfNotExist(primaryNodeType, parentNode, node);
        }
    }

    /**
     * Checks if the new node already exist, otherwise it will be created.
     *
     * @param primaryNodeType
     * @param parentNode
     * @param node
     * @return
     * @throws RepositoryException
     * @throws PathNotFoundException
     * @throws ItemExistsException
     * @throws NoSuchNodeTypeException
     * @throws LockException
     * @throws VersionException
     * @throws ConstraintViolationException
     */
    private Node createNewSubNodeIfNotExist(String primaryNodeType, Node parentNode, String node) throws RepositoryException, PathNotFoundException, ItemExistsException, NoSuchNodeTypeException, LockException, VersionException,
            ConstraintViolationException {

        if (parentNode.hasNode(node)) {
            parentNode = parentNode.getNode(node);
            versioningManager.checkOutContentObject(parentNode.getPath());
        } else {
            versioningManager.checkOutContentObject(parentNode.getPath());

            Node newNode = addNewNode(primaryNodeType, parentNode, node);

            versioningManager.addContentToCheckInList(newNode.getPath());
            parentNode = newNode;
        }

        return parentNode;
    }

    /**
     * The method adds a new node to its parent, write the versioning mixin and
     * set the primary node type.
     *
     * @param primaryNodeType
     * @param parentNode
     * @param node
     * @return
     * @throws ItemExistsException
     * @throws PathNotFoundException
     * @throws NoSuchNodeTypeException
     * @throws LockException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     */
    private Node addNewNode(String primaryNodeType, Node parentNode, String node) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException {
        Node newNode = parentNode.addNode(node, primaryNodeType);
        newNode.addMixin(ConstantsJackrabbit.MIXIN_VERSIONING);
        if (!ConstantsJackrabbit.ROOTPATH.equals(parentNode.getPath())) {
            newNode.setPrimaryType(parentNode.getPrimaryNodeType().getName());
        }
        return newNode;
    }
}
