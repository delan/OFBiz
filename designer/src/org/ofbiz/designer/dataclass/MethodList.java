/**
 * MethodList.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

package org.ofbiz.designer.dataclass;

import java.util.Vector;
import java.util.Enumeration;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class MethodList implements IMethodList
  {
  public Vector _Method = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.dataclass.MethodList" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Method
  
  public void addMethod( IMethod arg0  )
    {
    if( _Method != null )
      _Method.addElement( arg0 );
    }
  
  public int getMethodCount()
    {
    return _Method == null ? 0 : _Method.size();
    }
  
  public void setMethods( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Method = null;
      return;
      }

    _Method = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Method.addElement( string );
      }
    }
  
  public IMethod[] getMethods()
    {
    if( _Method == null )
      return null;

    IMethod[] array = new IMethod[ _Method.size() ];
    _Method.copyInto( array );

    return array;
    }
  
  public void setMethods( IMethod[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Method = v ;
    }
  
  public Enumeration getMethodElements()
    {
    return _Method == null ? null : _Method.elements();
    }
  
  public IMethod getMethodAt( int arg0 )
    {
    return _Method == null ? null :  (IMethod) _Method.elementAt( arg0 );
    }
  
  public void insertMethodAt( IMethod arg0, int arg1 )
    {
    if( _Method != null )
      _Method.insertElementAt( arg0, arg1 );
    }
  
  public void setMethodAt( IMethod arg0, int arg1 )
    {
    if( _Method != null )
      _Method.setElementAt( arg0, arg1 );
    }
  
  public boolean removeMethod( IMethod arg0 )
    {
    return _Method == null ? false : _Method.removeElement( arg0 );
    }
  
  public void removeMethodAt( int arg0 )
    {
    if( _Method == null )
      return;

    _Method.removeElementAt( arg0 );
    }
  
  public void removeAllMethods()
    {
    if( _Method == null )
      return;

    _Method.removeAllElements();
    }
  }