/**
 * RoleRelationship.java	Java 1.3.0 Fri Apr 27 15:06:26 EDT 2001
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

import java.util.Hashtable;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IIDRefBinding;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class RoleRelationship implements IRoleRelationship
  {
  public Hashtable _Attributes = new Hashtable();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.roledomain.RoleRelationship" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Attributes
  
  public String getAttribute( String name )
    {
    String value = (String) _Attributes.get( name );

    if( value != null ) 
      return value;

    return null;
    }
  
  public Hashtable getAttributes()
    {
    Hashtable clone = (Hashtable) _Attributes.clone();

    return clone;
    }
  
  public void setAttribute( String name, String value )
    {
    _Attributes.put( name, value );
    }
  
  public String removeAttribute( String name )
    {
    return (String) _Attributes.remove( name );
    }
  
  public String getLowRoleAttribute()
    {
    return getAttribute( "lowRole" );
    }
  
  public void setLowRoleAttribute( String value )
    {
    setAttribute( "lowRole", value );
    }
  
  public String removeLowRoleAttribute()
    {
    return removeAttribute( "lowRole" );
    }
  
  public Object getLowRoleReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "lowRole" );
    return key == null ? null : xml.getIdRef( key );
    }
  
  public String getHighRoleAttribute()
    {
    return getAttribute( "highRole" );
    }
  
  public void setHighRoleAttribute( String value )
    {
    setAttribute( "highRole", value );
    }
  
  public String removeHighRoleAttribute()
    {
    return removeAttribute( "highRole" );
    }
  
  public Object getHighRoleReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "highRole" );
    return key == null ? null : xml.getIdRef( key );
    }
  
  public String getIdAttribute()
    {
    return getAttribute( "id" );
    }
  
  public void setIdAttribute( String value )
    {
    setAttribute( "id", value );
    }
  
  public String removeIdAttribute()
    {
    return removeAttribute( "id" );
    }
  }