/**
 * ForwardMappingList.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class ForwardMappingList implements IForwardMappingList
  {
  public Vector _CorbaMapping = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.ForwardMappingList" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element CorbaMapping
  
  public void addCorbaMapping( ICorbaMapping arg0  )
    {
    if( _CorbaMapping != null )
      _CorbaMapping.addElement( arg0 );
    }
  
  public int getCorbaMappingCount()
    {
    return _CorbaMapping == null ? 0 : _CorbaMapping.size();
    }
  
  public void setCorbaMappings( Vector arg0 )
    {
    if( arg0 == null )
      {
      _CorbaMapping = null;
      return;
      }

    _CorbaMapping = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _CorbaMapping.addElement( string );
      }
    }
  
  public ICorbaMapping[] getCorbaMappings()
    {
    if( _CorbaMapping == null )
      return null;

    ICorbaMapping[] array = new ICorbaMapping[ _CorbaMapping.size() ];
    _CorbaMapping.copyInto( array );

    return array;
    }
  
  public void setCorbaMappings( ICorbaMapping[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _CorbaMapping = v ;
    }
  
  public Enumeration getCorbaMappingElements()
    {
    return _CorbaMapping == null ? null : _CorbaMapping.elements();
    }
  
  public ICorbaMapping getCorbaMappingAt( int arg0 )
    {
    return _CorbaMapping == null ? null :  (ICorbaMapping) _CorbaMapping.elementAt( arg0 );
    }
  
  public void insertCorbaMappingAt( ICorbaMapping arg0, int arg1 )
    {
    if( _CorbaMapping != null )
      _CorbaMapping.insertElementAt( arg0, arg1 );
    }
  
  public void setCorbaMappingAt( ICorbaMapping arg0, int arg1 )
    {
    if( _CorbaMapping != null )
      _CorbaMapping.setElementAt( arg0, arg1 );
    }
  
  public boolean removeCorbaMapping( ICorbaMapping arg0 )
    {
    return _CorbaMapping == null ? false : _CorbaMapping.removeElement( arg0 );
    }
  
  public void removeCorbaMappingAt( int arg0 )
    {
    if( _CorbaMapping == null )
      return;

    _CorbaMapping.removeElementAt( arg0 );
    }
  
  public void removeAllCorbaMappings()
    {
    if( _CorbaMapping == null )
      return;

    _CorbaMapping.removeAllElements();
    }
  }