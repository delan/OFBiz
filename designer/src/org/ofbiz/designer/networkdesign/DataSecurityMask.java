/**
 * DataSecurityMask.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class DataSecurityMask implements IDataSecurityMask
  {
  public Hashtable _Attributes = new Hashtable();
  public Vector _FieldMask = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.DataSecurityMask" );
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
  
  public String getDatanameAttribute()
    {
    return getAttribute( "dataname" );
    }
  
  public void setDatanameAttribute( String value )
    {
    setAttribute( "dataname", value );
    }
  
  public String removeDatanameAttribute()
    {
    return removeAttribute( "dataname" );
    }

  // element FieldMask
  
  public void addFieldMask( IFieldMask arg0  )
    {
    if( _FieldMask != null )
      _FieldMask.addElement( arg0 );
    }
  
  public int getFieldMaskCount()
    {
    return _FieldMask == null ? 0 : _FieldMask.size();
    }
  
  public void setFieldMasks( Vector arg0 )
    {
    if( arg0 == null )
      {
      _FieldMask = null;
      return;
      }

    _FieldMask = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _FieldMask.addElement( string );
      }
    }
  
  public IFieldMask[] getFieldMasks()
    {
    if( _FieldMask == null )
      return null;

    IFieldMask[] array = new IFieldMask[ _FieldMask.size() ];
    _FieldMask.copyInto( array );

    return array;
    }
  
  public void setFieldMasks( IFieldMask[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _FieldMask = v ;
    }
  
  public Enumeration getFieldMaskElements()
    {
    return _FieldMask == null ? null : _FieldMask.elements();
    }
  
  public IFieldMask getFieldMaskAt( int arg0 )
    {
    return _FieldMask == null ? null :  (IFieldMask) _FieldMask.elementAt( arg0 );
    }
  
  public void insertFieldMaskAt( IFieldMask arg0, int arg1 )
    {
    if( _FieldMask != null )
      _FieldMask.insertElementAt( arg0, arg1 );
    }
  
  public void setFieldMaskAt( IFieldMask arg0, int arg1 )
    {
    if( _FieldMask != null )
      _FieldMask.setElementAt( arg0, arg1 );
    }
  
  public boolean removeFieldMask( IFieldMask arg0 )
    {
    return _FieldMask == null ? false : _FieldMask.removeElement( arg0 );
    }
  
  public void removeFieldMaskAt( int arg0 )
    {
    if( _FieldMask == null )
      return;

    _FieldMask.removeElementAt( arg0 );
    }
  
  public void removeAllFieldMasks()
    {
    if( _FieldMask == null )
      return;

    _FieldMask.removeAllElements();
    }
  }