/**
 * Role.java	Java 1.3.0 Fri Apr 27 15:06:26 EDT 2001
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
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class Role implements IRole
  {
  public Hashtable _Attributes = new Hashtable();
  public String _Name = null;
  public String _Description = null;
  public String _Privileges = null;
  public IColor _Color = null;
  public IPosition _Position = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.roledomain.Role" );
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

  // element Name
  
  public String getName()
    {
    return _Name == null ? null : _Name;
    }
  
  public void setName( String arg0 )
    {
    _Name = arg0 == null ? null : new String( arg0 );
    }

  // element Description
  
  public String getDescription()
    {
    return _Description == null ? null : _Description;
    }
  
  public void setDescription( String arg0 )
    {
    _Description = arg0 == null ? null : new String( arg0 );
    }

  // element Privileges
  
  public String getPrivileges()
    {
    return _Privileges == null ? null : _Privileges;
    }
  
  public void setPrivileges( String arg0 )
    {
    _Privileges = arg0 == null ? null : new String( arg0 );
    }

  // element Color
  
  public IColor getColor()
    {
    return _Color;
    }
  
  public void setColor( IColor arg0 )
    {
    _Color = arg0;
    }

  // element Position
  
  public IPosition getPosition()
    {
    return _Position;
    }
  
  public void setPosition( IPosition arg0 )
    {
    _Position = arg0;
    }
  }