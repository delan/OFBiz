/**
 * Compartment.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

package org.ofbiz.designer.networkdesign;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IIDRefBinding;
import org.ofbiz.wrappers.xml.IClassDeclaration;
import java.util.StringTokenizer;

public class Compartment implements ICompartment
  {
  public Hashtable _Attributes = new Hashtable();
  public Vector _Compartment = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.Compartment" );
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
  
  public String getXAttribute()
    {
    return getAttribute( "x" );
    }
  
  public void setXAttribute( String value )
    {
    setAttribute( "x", value );
    }
  
  public String removeXAttribute()
    {
    return removeAttribute( "x" );
    }
  
  public String getHeightAttribute()
    {
    return getAttribute( "height" );
    }
  
  public void setHeightAttribute( String value )
    {
    setAttribute( "height", value );
    }
  
  public String removeHeightAttribute()
    {
    return removeAttribute( "height" );
    }
  
  public String getNameAttribute()
    {
    return getAttribute( "name" );
    }
  
  public void setNameAttribute( String value )
    {
    setAttribute( "name", value );
    }
  
  public String removeNameAttribute()
    {
    return removeAttribute( "name" );
    }
  
  public String getWidthAttribute()
    {
    return getAttribute( "width" );
    }
  
  public void setWidthAttribute( String value )
    {
    setAttribute( "width", value );
    }
  
  public String removeWidthAttribute()
    {
    return removeAttribute( "width" );
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
  
  public String getTasksAttribute()
    {
    return getAttribute( "tasks" );
    }
  
  public void setTasksAttribute( String value )
    {
    setAttribute( "tasks", value );
    }
  
  public String removeTasksAttribute()
    {
    return removeAttribute( "tasks" );
    }
  
  public Vector getTasksReference( IIDRefBinding xml )
    {
    Vector v = new Vector();
    String keys = (String) _Attributes.get( "tasks" );

    if( keys != null )
      {
      for( Enumeration e = new StringTokenizer( keys ); e.hasMoreElements(); )
        v.addElement( xml.getIdRef( (String) e.nextElement() ) );
      }

    return v;
    }
  
  public String getYAttribute()
    {
    return getAttribute( "y" );
    }
  
  public void setYAttribute( String value )
    {
    setAttribute( "y", value );
    }
  
  public String removeYAttribute()
    {
    return removeAttribute( "y" );
    }

  // element Compartment
  
  public void addCompartment( ICompartment arg0  )
    {
    if( _Compartment != null )
      _Compartment.addElement( arg0 );
    }
  
  public int getCompartmentCount()
    {
    return _Compartment == null ? 0 : _Compartment.size();
    }
  
  public void setCompartments( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Compartment = null;
      return;
      }

    _Compartment = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Compartment.addElement( string );
      }
    }
  
  public ICompartment[] getCompartments()
    {
    if( _Compartment == null )
      return null;

    ICompartment[] array = new ICompartment[ _Compartment.size() ];
    _Compartment.copyInto( array );

    return array;
    }
  
  public void setCompartments( ICompartment[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Compartment = v ;
    }
  
  public Enumeration getCompartmentElements()
    {
    return _Compartment == null ? null : _Compartment.elements();
    }
  
  public ICompartment getCompartmentAt( int arg0 )
    {
    return _Compartment == null ? null :  (ICompartment) _Compartment.elementAt( arg0 );
    }
  
  public void insertCompartmentAt( ICompartment arg0, int arg1 )
    {
    if( _Compartment != null )
      _Compartment.insertElementAt( arg0, arg1 );
    }
  
  public void setCompartmentAt( ICompartment arg0, int arg1 )
    {
    if( _Compartment != null )
      _Compartment.setElementAt( arg0, arg1 );
    }
  
  public boolean removeCompartment( ICompartment arg0 )
    {
    return _Compartment == null ? false : _Compartment.removeElement( arg0 );
    }
  
  public void removeCompartmentAt( int arg0 )
    {
    if( _Compartment == null )
      return;

    _Compartment.removeElementAt( arg0 );
    }
  
  public void removeAllCompartments()
    {
    if( _Compartment == null )
      return;

    _Compartment.removeAllElements();
    }
  }