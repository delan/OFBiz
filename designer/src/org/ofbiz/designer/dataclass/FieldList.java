/**
 * FieldList.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public class FieldList implements IFieldList
  {
  public Vector _Field = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.dataclass.FieldList" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Field
  
  public void addField( IField arg0  )
    {
    if( _Field != null )
      _Field.addElement( arg0 );
    }
  
  public int getFieldCount()
    {
    return _Field == null ? 0 : _Field.size();
    }
  
  public void setFields( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Field = null;
      return;
      }

    _Field = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Field.addElement( string );
      }
    }
  
  public IField[] getFields()
    {
    if( _Field == null )
      return null;

    IField[] array = new IField[ _Field.size() ];
    _Field.copyInto( array );

    return array;
    }
  
  public void setFields( IField[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Field = v ;
    }
  
  public Enumeration getFieldElements()
    {
    return _Field == null ? null : _Field.elements();
    }
  
  public IField getFieldAt( int arg0 )
    {
    return _Field == null ? null :  (IField) _Field.elementAt( arg0 );
    }
  
  public void insertFieldAt( IField arg0, int arg1 )
    {
    if( _Field != null )
      _Field.insertElementAt( arg0, arg1 );
    }
  
  public void setFieldAt( IField arg0, int arg1 )
    {
    if( _Field != null )
      _Field.setElementAt( arg0, arg1 );
    }
  
  public boolean removeField( IField arg0 )
    {
    return _Field == null ? false : _Field.removeElement( arg0 );
    }
  
  public void removeFieldAt( int arg0 )
    {
    if( _Field == null )
      return;

    _Field.removeElementAt( arg0 );
    }
  
  public void removeAllFields()
    {
    if( _Field == null )
      return;

    _Field.removeAllElements();
    }
  }