/**
 * Method.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public class Method implements IMethod
  {
  public IType _Type = null;
  public String _Name = null;
  public IParametersList _ParametersList = null;
  public Vector _Exception = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.dataclass.Method" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Type
  
  public IType getType()
    {
    return _Type;
    }
  
  public void setType( IType arg0 )
    {
    _Type = arg0;
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

  // element ParametersList
  
  public IParametersList getParametersList()
    {
    return _ParametersList;
    }
  
  public void setParametersList( IParametersList arg0 )
    {
    _ParametersList = arg0;
    }

  // element Exception
  
  public void addException( String arg0  )
    {
    if( _Exception != null )
      _Exception.addElement( arg0 == null ? null : new String( arg0 ) );
    }
  
  public int getExceptionCount()
    {
    return _Exception == null ? 0 : _Exception.size();
    }
  
  public void setExceptions( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Exception = null;
      return;
      }

    _Exception = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Exception.addElement( string == null ? null : new String( string ) );
      }
    }
  
  public String[] getExceptions()
    {
    if( _Exception == null )
      return null;

    String[] array = new String[ _Exception.size() ];
    int i = 0;

    for( Enumeration e = _Exception.elements(); e.hasMoreElements(); i++ )
      array[ i ] = ((String) e.nextElement());

    return array;
    }
  
  public void setExceptions( String[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] == null ? null : new String( arg0[ i ] ) );
      }

    _Exception = v ;
    }
  
  public Enumeration getExceptionElements()
    {
    if( _Exception == null )
      return null;

    Vector v = new Vector();

    for( Enumeration e = _Exception.elements(); e.hasMoreElements(); )
      v.addElement( ((String) e.nextElement()) );

    return v.elements();
    }
  
  public String getExceptionAt( int arg0 )
    {
    return _Exception == null ? null :  ((String) _Exception.elementAt( arg0 ));
    }
  
  public void insertExceptionAt( String arg0, int arg1 )
    {
    if( _Exception != null )
      _Exception.insertElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public void setExceptionAt( String arg0, int arg1 )
    {
    if( _Exception != null )
      _Exception.setElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public boolean removeException( String arg0 )
    {
    if( _Exception == null )
      return false;

    int i = 0;

    for( Enumeration e = _Exception.elements(); e.hasMoreElements(); i++ )
      if( ((String) e.nextElement()).equals( arg0 ) )
        {
        _Exception.removeElementAt( i );
        return true;
        }

    return false;
    }
  
  public void removeExceptionAt( int arg0 )
    {
    if( _Exception == null )
      return;

    _Exception.removeElementAt( arg0 );
    }
  
  public void removeAllExceptions()
    {
    if( _Exception == null )
      return;

    _Exception.removeAllElements();
    }
  }