/**
 * TransactionalTaskRealization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class TransactionalTaskRealization implements ITransactionalTaskRealization
  {
  public Hashtable _Attributes = new Hashtable();
  public Vector _TransactionalInput = new Vector();
  public Vector _TransactionalOutput = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.TransactionalTaskRealization" );
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
  
  public String getUrlAttribute()
    {
    return getAttribute( "url" );
    }
  
  public void setUrlAttribute( String value )
    {
    setAttribute( "url", value );
    }
  
  public String removeUrlAttribute()
    {
    return removeAttribute( "url" );
    }
  
  public String getUserpasswordAttribute()
    {
    return getAttribute( "userpassword" );
    }
  
  public void setUserpasswordAttribute( String value )
    {
    setAttribute( "userpassword", value );
    }
  
  public String removeUserpasswordAttribute()
    {
    return removeAttribute( "userpassword" );
    }
  
  public String getDatabaseAttribute()
    {
    return getAttribute( "database" );
    }
  
  public void setDatabaseAttribute( String value )
    {
    setAttribute( "database", value );
    }
  
  public String removeDatabaseAttribute()
    {
    return removeAttribute( "database" );
    }
  
  public String getQueryAttribute()
    {
    return getAttribute( "query" );
    }
  
  public void setQueryAttribute( String value )
    {
    setAttribute( "query", value );
    }
  
  public String removeQueryAttribute()
    {
    return removeAttribute( "query" );
    }
  
  public String getUsernameAttribute()
    {
    return getAttribute( "username" );
    }
  
  public void setUsernameAttribute( String value )
    {
    setAttribute( "username", value );
    }
  
  public String removeUsernameAttribute()
    {
    return removeAttribute( "username" );
    }

  // element TransactionalInput
  
  public void addTransactionalInput( String arg0  )
    {
    if( _TransactionalInput != null )
      _TransactionalInput.addElement( arg0 == null ? null : new String( arg0 ) );
    }
  
  public int getTransactionalInputCount()
    {
    return _TransactionalInput == null ? 0 : _TransactionalInput.size();
    }
  
  public void setTransactionalInputs( Vector arg0 )
    {
    if( arg0 == null )
      {
      _TransactionalInput = null;
      return;
      }

    _TransactionalInput = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _TransactionalInput.addElement( string == null ? null : new String( string ) );
      }
    }
  
  public String[] getTransactionalInputs()
    {
    if( _TransactionalInput == null )
      return null;

    String[] array = new String[ _TransactionalInput.size() ];
    int i = 0;

    for( Enumeration e = _TransactionalInput.elements(); e.hasMoreElements(); i++ )
      array[ i ] = ((String) e.nextElement());

    return array;
    }
  
  public void setTransactionalInputs( String[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] == null ? null : new String( arg0[ i ] ) );
      }

    _TransactionalInput = v ;
    }
  
  public Enumeration getTransactionalInputElements()
    {
    if( _TransactionalInput == null )
      return null;

    Vector v = new Vector();

    for( Enumeration e = _TransactionalInput.elements(); e.hasMoreElements(); )
      v.addElement( ((String) e.nextElement()) );

    return v.elements();
    }
  
  public String getTransactionalInputAt( int arg0 )
    {
    return _TransactionalInput == null ? null :  ((String) _TransactionalInput.elementAt( arg0 ));
    }
  
  public void insertTransactionalInputAt( String arg0, int arg1 )
    {
    if( _TransactionalInput != null )
      _TransactionalInput.insertElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public void setTransactionalInputAt( String arg0, int arg1 )
    {
    if( _TransactionalInput != null )
      _TransactionalInput.setElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public boolean removeTransactionalInput( String arg0 )
    {
    if( _TransactionalInput == null )
      return false;

    int i = 0;

    for( Enumeration e = _TransactionalInput.elements(); e.hasMoreElements(); i++ )
      if( ((String) e.nextElement()).equals( arg0 ) )
        {
        _TransactionalInput.removeElementAt( i );
        return true;
        }

    return false;
    }
  
  public void removeTransactionalInputAt( int arg0 )
    {
    if( _TransactionalInput == null )
      return;

    _TransactionalInput.removeElementAt( arg0 );
    }
  
  public void removeAllTransactionalInputs()
    {
    if( _TransactionalInput == null )
      return;

    _TransactionalInput.removeAllElements();
    }

  // element TransactionalOutput
  
  public void addTransactionalOutput( String arg0  )
    {
    if( _TransactionalOutput != null )
      _TransactionalOutput.addElement( arg0 == null ? null : new String( arg0 ) );
    }
  
  public int getTransactionalOutputCount()
    {
    return _TransactionalOutput == null ? 0 : _TransactionalOutput.size();
    }
  
  public void setTransactionalOutputs( Vector arg0 )
    {
    if( arg0 == null )
      {
      _TransactionalOutput = null;
      return;
      }

    _TransactionalOutput = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _TransactionalOutput.addElement( string == null ? null : new String( string ) );
      }
    }
  
  public String[] getTransactionalOutputs()
    {
    if( _TransactionalOutput == null )
      return null;

    String[] array = new String[ _TransactionalOutput.size() ];
    int i = 0;

    for( Enumeration e = _TransactionalOutput.elements(); e.hasMoreElements(); i++ )
      array[ i ] = ((String) e.nextElement());

    return array;
    }
  
  public void setTransactionalOutputs( String[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] == null ? null : new String( arg0[ i ] ) );
      }

    _TransactionalOutput = v ;
    }
  
  public Enumeration getTransactionalOutputElements()
    {
    if( _TransactionalOutput == null )
      return null;

    Vector v = new Vector();

    for( Enumeration e = _TransactionalOutput.elements(); e.hasMoreElements(); )
      v.addElement( ((String) e.nextElement()) );

    return v.elements();
    }
  
  public String getTransactionalOutputAt( int arg0 )
    {
    return _TransactionalOutput == null ? null :  ((String) _TransactionalOutput.elementAt( arg0 ));
    }
  
  public void insertTransactionalOutputAt( String arg0, int arg1 )
    {
    if( _TransactionalOutput != null )
      _TransactionalOutput.insertElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public void setTransactionalOutputAt( String arg0, int arg1 )
    {
    if( _TransactionalOutput != null )
      _TransactionalOutput.setElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public boolean removeTransactionalOutput( String arg0 )
    {
    if( _TransactionalOutput == null )
      return false;

    int i = 0;

    for( Enumeration e = _TransactionalOutput.elements(); e.hasMoreElements(); i++ )
      if( ((String) e.nextElement()).equals( arg0 ) )
        {
        _TransactionalOutput.removeElementAt( i );
        return true;
        }

    return false;
    }
  
  public void removeTransactionalOutputAt( int arg0 )
    {
    if( _TransactionalOutput == null )
      return;

    _TransactionalOutput.removeElementAt( arg0 );
    }
  
  public void removeAllTransactionalOutputs()
    {
    if( _TransactionalOutput == null )
      return;

    _TransactionalOutput.removeAllElements();
    }
  }