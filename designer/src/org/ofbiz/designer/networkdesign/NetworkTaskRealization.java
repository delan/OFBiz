/**
 * NetworkTaskRealization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class NetworkTaskRealization implements INetworkTaskRealization
  {
  public Hashtable _Attributes = new Hashtable();
  public Vector _Domain = new Vector();
  public IInputMappingList _InputMappingList = null;
  public IOutputMappingList _OutputMappingList = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.NetworkTaskRealization" );
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
  
  public String getLasttaskAttribute()
    {
    return getAttribute( "lasttask" );
    }
  
  public void setLasttaskAttribute( String value )
    {
    setAttribute( "lasttask", value );
    }
  
  public String removeLasttaskAttribute()
    {
    return removeAttribute( "lasttask" );
    }
  
  public Object getLasttaskReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "lasttask" );
    return key == null ? null : xml.getIdRef( key );
    }
  
  public String getRealizationtypeAttribute()
    {
    return getAttribute( "realizationtype" );
    }
  
  public void setRealizationtypeAttribute( String value )
    {
    setAttribute( "realizationtype", value );
    }
  
  public String removeRealizationtypeAttribute()
    {
    return removeAttribute( "realizationtype" );
    }
  
  public String getFirsttaskAttribute()
    {
    return getAttribute( "firsttask" );
    }
  
  public void setFirsttaskAttribute( String value )
    {
    setAttribute( "firsttask", value );
    }
  
  public String removeFirsttaskAttribute()
    {
    return removeAttribute( "firsttask" );
    }
  
  public Object getFirsttaskReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "firsttask" );
    return key == null ? null : xml.getIdRef( key );
    }

  // element Domain
  
  public void addDomain( IDomain arg0  )
    {
    if( _Domain != null )
      _Domain.addElement( arg0 );
    }
  
  public int getDomainCount()
    {
    return _Domain == null ? 0 : _Domain.size();
    }
  
  public void setDomains( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Domain = null;
      return;
      }

    _Domain = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Domain.addElement( string );
      }
    }
  
  public IDomain[] getDomains()
    {
    if( _Domain == null )
      return null;

    IDomain[] array = new IDomain[ _Domain.size() ];
    _Domain.copyInto( array );

    return array;
    }
  
  public void setDomains( IDomain[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Domain = v ;
    }
  
  public Enumeration getDomainElements()
    {
    return _Domain == null ? null : _Domain.elements();
    }
  
  public IDomain getDomainAt( int arg0 )
    {
    return _Domain == null ? null :  (IDomain) _Domain.elementAt( arg0 );
    }
  
  public void insertDomainAt( IDomain arg0, int arg1 )
    {
    if( _Domain != null )
      _Domain.insertElementAt( arg0, arg1 );
    }
  
  public void setDomainAt( IDomain arg0, int arg1 )
    {
    if( _Domain != null )
      _Domain.setElementAt( arg0, arg1 );
    }
  
  public boolean removeDomain( IDomain arg0 )
    {
    if( _Domain == null )
      return false;

    return  _Domain.removeElement( arg0 );
    }
  
  public void removeDomainAt( int arg0 )
    {
    if( _Domain == null )
      return;

    Vector v = (Vector) _Domain.clone();
    v.removeElementAt( arg0 );


    _Domain.removeElementAt( arg0 );
    }

  // element InputMappingList
  
  public IInputMappingList getInputMappingList()
    {
    return _InputMappingList;
    }
  
  public void setInputMappingList( IInputMappingList arg0 )
    {
    _InputMappingList = arg0;
    }

  // element OutputMappingList
  
  public IOutputMappingList getOutputMappingList()
    {
    return _OutputMappingList;
    }
  
  public void setOutputMappingList( IOutputMappingList arg0 )
    {
    _OutputMappingList = arg0;
    }
  }