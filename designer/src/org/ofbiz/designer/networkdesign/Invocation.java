/**
 * Invocation.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class Invocation implements IInvocation
  {
  public Vector _Parameter = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.Invocation" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Parameter
  
  public void addParameter( IParameter arg0  )
    {
    if( _Parameter != null )
      _Parameter.addElement( arg0 );
    }
  
  public int getParameterCount()
    {
    return _Parameter == null ? 0 : _Parameter.size();
    }
  
  public void setParameters( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Parameter = null;
      return;
      }

    _Parameter = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Parameter.addElement( string );
      }
    }
  
  public IParameter[] getParameters()
    {
    if( _Parameter == null )
      return null;

    IParameter[] array = new IParameter[ _Parameter.size() ];
    _Parameter.copyInto( array );

    return array;
    }
  
  public void setParameters( IParameter[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Parameter = v ;
    }
  
  public Enumeration getParameterElements()
    {
    return _Parameter == null ? null : _Parameter.elements();
    }
  
  public IParameter getParameterAt( int arg0 )
    {
    return _Parameter == null ? null :  (IParameter) _Parameter.elementAt( arg0 );
    }
  
  public void insertParameterAt( IParameter arg0, int arg1 )
    {
    if( _Parameter != null )
      _Parameter.insertElementAt( arg0, arg1 );
    }
  
  public void setParameterAt( IParameter arg0, int arg1 )
    {
    if( _Parameter != null )
      _Parameter.setElementAt( arg0, arg1 );
    }
  
  public boolean removeParameter( IParameter arg0 )
    {
    return _Parameter == null ? false : _Parameter.removeElement( arg0 );
    }
  
  public void removeParameterAt( int arg0 )
    {
    if( _Parameter == null )
      return;

    _Parameter.removeElementAt( arg0 );
    }
  
  public void removeAllParameters()
    {
    if( _Parameter == null )
      return;

    _Parameter.removeAllElements();
    }
  }