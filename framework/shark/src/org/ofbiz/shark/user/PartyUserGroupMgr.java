/*
 * $Id$
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.user;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.base.util.UtilMisc;

import org.enhydra.shark.api.internal.usergroup.UserGroupManager;
import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.UserTransaction;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class PartyUserGroupMgr implements UserGroupManager {

    public static final String module = PartyUserGroupMgr.class.getName();
    protected CallbackUtilities callBack = null;
    protected GenericDelegator delegator = null;

    public void configure(CallbackUtilities cb) throws RootException {
        this.delegator = SharkContainer.getDelegator();
        this.callBack = cb;
    }

    public List getAllGroupnames(UserTransaction trans) throws RootException {
        return null;  // TODO: Implement Me!
    }

    public List getAllUsers(UserTransaction trans) throws RootException {
        List userLogins = null;
        List allUsers = null;
        try {
            userLogins = delegator.findAll("UserLogin", UtilMisc.toList("userLoginId"));
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }

        if (userLogins != null) {
            allUsers = new ArrayList();
            Iterator i = userLogins.iterator();
            while (i.hasNext()) {
                GenericValue userLogin = (GenericValue) i.next();
                allUsers.add(userLogin.getString("userLoginId"));
            }
        }
        return allUsers;
    }

    public List getAllUsers(UserTransaction trans, String groupName) throws RootException {
        // TODO: Implement Me!
        return this.getAllUsers(trans);
    }

    public List getAllUsers(UserTransaction trans, List groupNames) throws RootException {
        // TODO: Implement Me!
        return null;  // TODO: Implement Me!
    }

    public List getAllImmediateUsers(UserTransaction trans, String groupName) throws RootException {
        // TODO: Implement Me!
        return this.getAllUsers(trans);
    }

    public List getAllSubgroups(UserTransaction trans, String groupName) throws RootException {
        // TODO: Implement Me!
        return null;
    }

    public List getAllSubgroups(UserTransaction trans, List groupNames) throws RootException {
        // TODO: Implement Me!
        return null;
    }

    public List getAllImmediateSubgroups(UserTransaction trans, String groupName) throws RootException {
        // TODO: Implement Me!
        return null;
    }

    public void createGroup(UserTransaction trans, String s, String s1) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void removeGroup(UserTransaction trans, String groupName) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public boolean doesGroupExist(UserTransaction trans, String groupName) throws RootException {
        return false;  // TODO: Implement Me!
    }

    public boolean doesGroupBelongToGroup(UserTransaction trans, String groupName, String subGroupName) throws RootException {
        return false;  // TODO: Implement Me!
    }

    public void updateGroup(UserTransaction trans, String s, String s1) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void addGroupToGroup(UserTransaction trans, String s, String s1) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void removeGroupFromGroup(UserTransaction trans, String s, String s1) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void removeGroupTree(UserTransaction trans, String s) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void removeUsersFromGroupTree(UserTransaction trans, String s) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void moveGroup(UserTransaction trans, String s, String s1, String s2) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public String getGroupDescription(UserTransaction trans, String s) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void addUserToGroup(UserTransaction trans, String s, String s1) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void removeUserFromGroup(UserTransaction trans, String s, String s1) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void moveUser(UserTransaction trans, String s, String s1, String s2) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public boolean doesUserBelongToGroup(UserTransaction trans, String groupName, String username) throws RootException {
        return false;  // TODO: Implement Me!
    }

    public void createUser(UserTransaction trans, String groupName, String username, String password, String firstname, String lastname, String email) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void updateUser(UserTransaction trans, String username, String firstname, String lastname, String email) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public void removeUser(UserTransaction trans, String username) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public boolean doesUserExist(UserTransaction trans, String username) throws RootException {
        GenericValue userLogin = this.getUserLogin(username);
        return (userLogin != null);
    }

    public void setPassword(UserTransaction trans, String username, String password) throws RootException {
        throw new RootException("PartyUserGroupMgr does not implement create/update/remove methods. Use the party manager instead!");
    }

    public String getUserRealName(UserTransaction trans, String username) throws RootException {
        return username;
    }

    public String getUserFirstName(UserTransaction trans, String username) throws RootException {
        return username;
    }

    public String getUserLastName(UserTransaction trans, String username) throws RootException {
        return username;
    }

    public String getUserEMailAddress(UserTransaction trans, String username) throws RootException {
        return username;
    }

    protected GenericValue getUserLogin(String username) throws RootException {
        GenericValue userLogin = null;
        try {
            userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", username));
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }
        return userLogin;
    }
}
