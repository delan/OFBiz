/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project and repected authors.
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.core.security;


import org.ofbiz.core.util.UtilCache;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.EntityUtil;
import org.ofbiz.core.entity.GenericValue;

import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;


/**
 * <code>OFBizSecurity</code>
 * This class has not been altered from the original source. It now just extends Security and was therefore renamed to
 * OFBizSecurity.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version 1.0
 */
public class OFBizSecurity extends Security {

    /** Hashtable to cache a Collection of UserLoginSecurityGroup entities for each UserLogin, by userLoginId.
     */
    public static UtilCache userLoginSecurityGroupByUserLoginId = new UtilCache("security.UserLoginSecurityGroupByUserLoginId");

    /** Hashtable to cache whether or not a certain SecurityGroupPermission row exists or not.
     * For each SecurityGroupPermissionPK there is a Boolean in the cache specifying whether or not it exists.
     * In this way the cache speeds things up whether or not the user has a permission.
     */
    public static UtilCache securityGroupPermissionCache = new UtilCache("security.SecurityGroupPermissionCache");

    GenericDelegator delegator = null;

    protected OFBizSecurity() {}

    protected OFBizSecurity(GenericDelegator delegator) {
        this.delegator = delegator;
    }

    public GenericDelegator getDelegator() {
        return delegator;
    }

    public void setDelegator(GenericDelegator delegator) {
        this.delegator = delegator;
    }

    /** Uses userLoginSecurityGroupByUserLoginId cache to speed up the finding of the userLogin's security group list.
     * @param userLoginId The userLoginId to find security groups by
     * @return An iterator made from the Collection either cached or retrieved from the database through the UserLoginSecurityGroup Delegator.
     */
    public Iterator findUserLoginSecurityGroupByUserLoginId(String userLoginId) {
        List collection = (List) userLoginSecurityGroupByUserLoginId.get(userLoginId);

        if (collection == null) {
            try {
                collection = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId), null);
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
            // make an empty collection to speed up the case where a userLogin belongs to no security groups
            if (collection == null) collection = new LinkedList();
            userLoginSecurityGroupByUserLoginId.put(userLoginId, collection);
        }
        // filter each time after cache retreival, ie cache will contain entire list
        collection = EntityUtil.filterByDate(collection, true);
        return collection.iterator();
    }

    /** Finds whether or not a SecurityGroupPermission row exists given a groupId and permission.
     * Uses the securityGroupPermissionCache to speed this up.
     * The groupId,permission pair is cached instead of the userLoginId,permission pair to keep the cache small and to make it more changeable.
     * @param groupId The ID of the group
     * @param permission The name of the permission
     * @return boolean specifying whether or not a SecurityGroupPermission row exists
     */
    public boolean securityGroupPermissionExists(String groupId, String permission) {
        GenericValue securityGroupPermissionValue = delegator.makeValue("SecurityGroupPermission",
                UtilMisc.toMap("groupId", groupId, "permissionId", permission));
        Boolean exists = (Boolean) securityGroupPermissionCache.get(securityGroupPermissionValue);

        if (exists == null) {
            try {
                if (delegator.findByPrimaryKey(securityGroupPermissionValue.getPrimaryKey()) != null)
                    exists = Boolean.TRUE;
                else
                    exists = Boolean.FALSE;
            } catch (GenericEntityException e) {
                exists = Boolean.FALSE;
                Debug.logWarning(e);
            }
            securityGroupPermissionCache.put(securityGroupPermissionValue, exists);
        }
        return exists.booleanValue();
    }

    /** Checks to see if the currently logged in userLogin has the passed permission.
     * @param permission Name of the permission to check.
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasPermission(String permission, HttpSession session) {
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);

        if (userLogin == null) return false;

        return hasPermission(permission, userLogin);
    }

    /** Checks to see if the userLogin has the passed permission.
     * @param permission Name of the permission to check.
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasPermission(String permission, GenericValue userLogin) {
        if (userLogin == null) return false;

        Iterator iterator = findUserLoginSecurityGroupByUserLoginId(userLogin.getString("userLoginId"));
        GenericValue userLoginSecurityGroup = null;

        while (iterator.hasNext()) {
            userLoginSecurityGroup = (GenericValue) iterator.next();
            if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), permission)) return true;
        }

        return false;
    }

    /** Like hasPermission above, except it has functionality specific to Entity permissions. Checks the entity for the specified action, as well as for "_ADMIN" to allow for simplified general administration permission.
     * @param entity The name of the Entity corresponding to the desired permission.
     * @param action The action on the Entity corresponding to the desired permission.
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasEntityPermission(String entity, String action, HttpSession session) {
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);

        if (userLogin == null) return false;
        return hasEntityPermission(entity, action, userLogin);
    }

    /** Like hasPermission above, except it has functionality specific to Entity permissions. Checks the entity for the specified action, as well as for "_ADMIN" to allow for simplified general administration permission.
     * @param entity The name of the Entity corresponding to the desired permission.
     * @param action The action on the Entity corresponding to the desired permission.
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasEntityPermission(String entity, String action, GenericValue userLogin) {
        if (userLogin == null) return false;

        // if (Debug.infoOn()) Debug.logInfo("hasEntityPermission: entity=" + entity + ", action=" + action);
        Iterator iterator = findUserLoginSecurityGroupByUserLoginId(userLogin.getString("userLoginId"));
        GenericValue userLoginSecurityGroup = null;

        while (iterator.hasNext()) {
            userLoginSecurityGroup = (GenericValue) iterator.next();

            // if (Debug.infoOn()) Debug.logInfo("hasEntityPermission: userLoginSecurityGroup=" + userLoginSecurityGroup.toString());

            // always try _ADMIN first so that it will cache first, keeping the cache smaller
            if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), entity + "_ADMIN"))
                return true;
            if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), entity + action))
                return true;
        }

        return false;
    }
}
