/**
 * Type.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public class Type implements IType
  {
  public ISimpleTypeOrUrl _SimpleTypeOrUrl = null;
  public Vector _Dimension = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.dataclass.Type" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element SimpleTypeOrUrl
  
  public ISimpleTypeOrUrl getSimpleTypeOrUrl()
    {
    return _SimpleTypeOrUrl;
    }
  
  public void setSimpleTypeOrUrl( ISimpleTypeOrUrl arg0 )
    {
    _SimpleTypeOrUrl = arg0;
    }

  // element Dimension
  
  public void addDimension( String arg0  )
    {
    if( _Dimension != null )
      _Dimension.addElement( arg0 == null ? null : new String( arg0 ) );
    }
  
  public int getDimensionCount()
    {
    return _Dimension == null ? 0 : _Dimension.size();
    }
  
  public void setDimensions( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Dimension = null;
      return;
      }

    _Dimension = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Dimension.addElement( string == null ? null : new String( string ) );
      }
    }
  
  public String[] getDimensions()
    {
    if( _Dimension == null )
      return null;

    String[] array = new String[ _Dimension.size() ];
    int i = 0;

    for( Enumeration e = _Dimension.elements(); e.hasMoreElements(); i++ )
      array[ i ] = ((String) e.nextElement());

    return array;
    }
  
  public void setDimensions( String[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] == null ? null : new String( arg0[ i ] ) );
      }

    _Dimension = v ;
    }
  
  public Enumeration getDimensionElements()
    {
    if( _Dimension == null )
      return null;

    Vector v = new Vector();

    for( Enumeration e = _Dimension.elements(); e.hasMoreElements(); )
      v.addElement( ((String) e.nextElement()) );

    return v.elements();
    }
  
  public String getDimensionAt( int arg0 )
    {
    return _Dimension == null ? null :  ((String) _Dimension.elementAt( arg0 ));
    }
  
  public void insertDimensionAt( String arg0, int arg1 )
    {
    if( _Dimension != null )
      _Dimension.insertElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public void setDimensionAt( String arg0, int arg1 )
    {
    if( _Dimension != null )
      _Dimension.setElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public boolean removeDimension( String arg0 )
    {
    if( _Dimension == null )
      return false;

    int i = 0;

    for( Enumeration e = _Dimension.elements(); e.hasMoreElements(); i++ )
      if( ((String) e.nextElement()).equals( arg0 ) )
        {
        _Dimension.removeElementAt( i );
        return true;
        }

    return false;
    }
  
  public void removeDimensionAt( int arg0 )
    {
    if( _Dimension == null )
      return;

    _Dimension.removeElementAt( arg0 );
    }
  
  public void removeAllDimensions()
    {
    if( _Dimension == null )
      return;

    _Dimension.removeAllElements();
    }
  }