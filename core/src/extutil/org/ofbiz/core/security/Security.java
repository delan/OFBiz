/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.core.security;

import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * Security handler: This class is an abstract implementation for all commononly used security aspects.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version    $Revision$
 * @since      2.0
 */
public abstract class Security {

    /**
     * Hashtable to cache a Collection of UserLoginSecurityGroup entities for each UserLogin, by userLoginId.
     */
    public static UtilCache userLoginSecurityGroupByUserLoginId = new UtilCache("security.UserLoginSecurityGroupByUserLoginId");

    /**
     * Hashtable to cache whether or not a certain SecurityGroupPermission row exists or not.
     * For each SecurityGroupPermissionPK there is a Boolean in the cache specifying whether or not it exists.
     * In this way the cache speeds things up whether or not the user has a permission.
     */
    public static UtilCache securityGroupPermissionCache = new UtilCache("security.SecurityGroupPermissionCache");

    GenericDelegator delegator = null;

    public GenericDelegator getDelegator() {
        return delegator;
    }

    public void setDelegator(GenericDelegator delegator) {
        this.delegator = delegator;
    }

    /**
     * Uses userLoginSecurityGroupByUserLoginId cache to speed up the finding of the userLogin's security group list.
     *
     * @param userLoginId The userLoginId to find security groups by
     * @return An iterator made from the Collection either cached or retrieved from the database through the
     * 		   UserLoginSecurityGroup Delegator.
     */
    public abstract Iterator findUserLoginSecurityGroupByUserLoginId(String userLoginId);

    /**
     * Finds whether or not a SecurityGroupPermission row exists given a groupId and permission.
     * Uses the securityGroupPermissionCache to speed this up.
     * The groupId,permission pair is cached instead of the userLoginId,permission pair to keep the cache small and to
     * make it more changeable.
     *
     * @param groupId The ID of the group
     * @param permission The name of the permission
     * @return boolean specifying whether or not a SecurityGroupPermission row exists
     */
    public abstract boolean securityGroupPermissionExists(String groupId, String permission);

    /**
     * Checks to see if the currently logged in userLogin has the passed permission.
     *
     * @param permission Name of the permission to check.
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public abstract boolean hasPermission(String permission, HttpSession session);

    /**
     * Checks to see if the userLogin has the passed permission.
     *
     * @param permission Name of the permission to check.
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public abstract boolean hasPermission(String permission, GenericValue userLogin);

    /**
     * Like hasPermission above, except it has functionality specific to Entity permissions. Checks the entity for the
     * specified action, as well as for "_ADMIN" to allow for simplified general administration permission.
     *
     * @param entity The name of the Entity corresponding to the desired permission.
     * @param action The action on the Entity corresponding to the desired permission.
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public abstract boolean hasEntityPermission(String entity, String action, HttpSession session);

    /**
     * Like hasPermission above, except it has functionality specific to Entity permissions. Checks the entity for the
     * specified action, as well as for "_ADMIN" to allow for simplified general administration permission.
     *
     * @param entity The name of the Entity corresponding to the desired permission.
     * @param action The action on the Entity corresponding to the desired permission.
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public abstract boolean hasEntityPermission(String entity, String action, GenericValue userLogin);
}
