/**
 * IRoleDomain.java	Java 1.3.0 Fri Apr 27 15:06:26 EDT 2001
 *
 * Copyright 1999 by ObjectSpace, Inc.,
 * 14850 Quorum Dr., Dallas, TX, 75240 U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of ObjectSpace, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with ObjectSpace.
 */

package org.ofbiz.designer.roledomain;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

public interface IRoleDomain extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getDateAttribute();
  public void setDateAttribute( String value );
  public String removeDateAttribute();
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();

  // element Name
  public String getName();
  public void setName( String arg0 );

  // element Description
  public String getDescription();
  public void setDescription( String arg0 );

  // element Role
  public void addRole( IRole arg0  );
  public int getRoleCount();
  public void setRoles( Vector arg0 );
  public IRole[] getRoles();
  public void setRoles( IRole[] arg0 );
  public Enumeration getRoleElements();
  public IRole getRoleAt( int arg0 );
  public void insertRoleAt( IRole arg0, int arg1 );
  public void setRoleAt( IRole arg0, int arg1 );
  public boolean removeRole( IRole arg0 );
  public void removeRoleAt( int arg0 );
  public void removeAllRoles();

  // element RoleRelationship
  public void addRoleRelationship( IRoleRelationship arg0  );
  public int getRoleRelationshipCount();
  public void setRoleRelationships( Vector arg0 );
  public IRoleRelationship[] getRoleRelationships();
  public void setRoleRelationships( IRoleRelationship[] arg0 );
  public Enumeration getRoleRelationshipElements();
  public IRoleRelationship getRoleRelationshipAt( int arg0 );
  public void insertRoleRelationshipAt( IRoleRelationship arg0, int arg1 );
  public void setRoleRelationshipAt( IRoleRelationship arg0, int arg1 );
  public boolean removeRoleRelationship( IRoleRelationship arg0 );
  public void removeRoleRelationshipAt( int arg0 );
  public void removeAllRoleRelationships();
  }