/**
 * InputMappingList.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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
import java.util.Enumeration;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class InputMappingList implements IInputMappingList
  {
  public Vector _Mapping = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.InputMappingList" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Mapping
  
  public void addMapping( IMapping arg0  )
    {
    if( _Mapping != null )
      _Mapping.addElement( arg0 );
    }
  
  public int getMappingCount()
    {
    return _Mapping == null ? 0 : _Mapping.size();
    }
  
  public void setMappings( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Mapping = null;
      return;
      }

    _Mapping = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Mapping.addElement( string );
      }
    }
  
  public IMapping[] getMappings()
    {
    if( _Mapping == null )
      return null;

    IMapping[] array = new IMapping[ _Mapping.size() ];
    _Mapping.copyInto( array );

    return array;
    }
  
  public void setMappings( IMapping[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Mapping = v ;
    }
  
  public Enumeration getMappingElements()
    {
    return _Mapping == null ? null : _Mapping.elements();
    }
  
  public IMapping getMappingAt( int arg0 )
    {
    return _Mapping == null ? null :  (IMapping) _Mapping.elementAt( arg0 );
    }
  
  public void insertMappingAt( IMapping arg0, int arg1 )
    {
    if( _Mapping != null )
      _Mapping.insertElementAt( arg0, arg1 );
    }
  
  public void setMappingAt( IMapping arg0, int arg1 )
    {
    if( _Mapping != null )
      _Mapping.setElementAt( arg0, arg1 );
    }
  
  public boolean removeMapping( IMapping arg0 )
    {
    return _Mapping == null ? false : _Mapping.removeElement( arg0 );
    }
  
  public void removeMappingAt( int arg0 )
    {
    if( _Mapping == null )
      return;

    _Mapping.removeElementAt( arg0 );
    }
  
  public void removeAllMappings()
    {
    if( _Mapping == null )
      return;

    _Mapping.removeAllElements();
    }
  }