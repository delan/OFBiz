/**
 * RoleDomain.java	Java 1.3.0 Fri Apr 27 15:06:26 EDT 2001
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
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class RoleDomain implements IRoleDomain
  {
  public Hashtable _Attributes = new Hashtable();
  public String _Name = null;
  public String _Description = null;
  public Vector _Role = new Vector();
  public Vector _RoleRelationship = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.roledomain.RoleDomain" );
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
  
  public String getDateAttribute()
    {
    return getAttribute( "date" );
    }
  
  public void setDateAttribute( String value )
    {
    setAttribute( "date", value );
    }
  
  public String removeDateAttribute()
    {
    return removeAttribute( "date" );
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

  // element Role
  
  public void addRole( IRole arg0  )
    {
    if( _Role != null )
      _Role.addElement( arg0 );
    }
  
  public int getRoleCount()
    {
    return _Role == null ? 0 : _Role.size();
    }
  
  public void setRoles( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Role = null;
      return;
      }

    _Role = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Role.addElement( string );
      }
    }
  
  public IRole[] getRoles()
    {
    if( _Role == null )
      return null;

    IRole[] array = new IRole[ _Role.size() ];
    _Role.copyInto( array );

    return array;
    }
  
  public void setRoles( IRole[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Role = v ;
    }
  
  public Enumeration getRoleElements()
    {
    return _Role == null ? null : _Role.elements();
    }
  
  public IRole getRoleAt( int arg0 )
    {
    return _Role == null ? null :  (IRole) _Role.elementAt( arg0 );
    }
  
  public void insertRoleAt( IRole arg0, int arg1 )
    {
    if( _Role != null )
      _Role.insertElementAt( arg0, arg1 );
    }
  
  public void setRoleAt( IRole arg0, int arg1 )
    {
    if( _Role != null )
      _Role.setElementAt( arg0, arg1 );
    }
  
  public boolean removeRole( IRole arg0 )
    {
    return _Role == null ? false : _Role.removeElement( arg0 );
    }
  
  public void removeRoleAt( int arg0 )
    {
    if( _Role == null )
      return;

    _Role.removeElementAt( arg0 );
    }
  
  public void removeAllRoles()
    {
    if( _Role == null )
      return;

    _Role.removeAllElements();
    }

  // element RoleRelationship
  
  public void addRoleRelationship( IRoleRelationship arg0  )
    {
    if( _RoleRelationship != null )
      _RoleRelationship.addElement( arg0 );
    }
  
  public int getRoleRelationshipCount()
    {
    return _RoleRelationship == null ? 0 : _RoleRelationship.size();
    }
  
  public void setRoleRelationships( Vector arg0 )
    {
    if( arg0 == null )
      {
      _RoleRelationship = null;
      return;
      }

    _RoleRelationship = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _RoleRelationship.addElement( string );
      }
    }
  
  public IRoleRelationship[] getRoleRelationships()
    {
    if( _RoleRelationship == null )
      return null;

    IRoleRelationship[] array = new IRoleRelationship[ _RoleRelationship.size() ];
    _RoleRelationship.copyInto( array );

    return array;
    }
  
  public void setRoleRelationships( IRoleRelationship[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _RoleRelationship = v ;
    }
  
  public Enumeration getRoleRelationshipElements()
    {
    return _RoleRelationship == null ? null : _RoleRelationship.elements();
    }
  
  public IRoleRelationship getRoleRelationshipAt( int arg0 )
    {
    return _RoleRelationship == null ? null :  (IRoleRelationship) _RoleRelationship.elementAt( arg0 );
    }
  
  public void insertRoleRelationshipAt( IRoleRelationship arg0, int arg1 )
    {
    if( _RoleRelationship != null )
      _RoleRelationship.insertElementAt( arg0, arg1 );
    }
  
  public void setRoleRelationshipAt( IRoleRelationship arg0, int arg1 )
    {
    if( _RoleRelationship != null )
      _RoleRelationship.setElementAt( arg0, arg1 );
    }
  
  public boolean removeRoleRelationship( IRoleRelationship arg0 )
    {
    return _RoleRelationship == null ? false : _RoleRelationship.removeElement( arg0 );
    }
  
  public void removeRoleRelationshipAt( int arg0 )
    {
    if( _RoleRelationship == null )
      return;

    _RoleRelationship.removeElementAt( arg0 );
    }
  
  public void removeAllRoleRelationships()
    {
    if( _RoleRelationship == null )
      return;

    _RoleRelationship.removeAllElements();
    }
  }