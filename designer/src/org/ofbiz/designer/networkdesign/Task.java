/**
 * Task.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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
import java.util.StringTokenizer;

public class Task implements ITask
  {
  public Hashtable _Attributes = new Hashtable();
  public String _Description = null;
  public Vector _Roles = new Vector();
  public IRealization _Realization = null;
  public Vector _Invocation = new Vector();
  public Vector _Output = new Vector();
  public Vector _TaskException = new Vector();
  public Vector _DataSecurityMask = new Vector();
  public Vector _Constraint = new Vector();
  public IInputOperator _InputOperator = null;
  public IOutputOperator _OutputOperator = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.Task" );
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
  
  public String getOutarcsAttribute()
    {
    return getAttribute( "outarcs" );
    }
  
  public void setOutarcsAttribute( String value )
    {
    setAttribute( "outarcs", value );
    }
  
  public String removeOutarcsAttribute()
    {
    return removeAttribute( "outarcs" );
    }
  
  public Vector getOutarcsReference( IIDRefBinding xml )
    {
    Vector v = new Vector();
    String keys = (String) _Attributes.get( "outarcs" );

    if( keys != null )
      {
      for( Enumeration e = new StringTokenizer( keys ); e.hasMoreElements(); )
        v.addElement( xml.getIdRef( (String) e.nextElement() ) );
      }

    return v;
    }
  
  public String getNameAttribute()
    {
    return getAttribute( "name" );
    }
  
  public void setNameAttribute( String value )
    {
    setAttribute( "name", value );
    }
  
  public String removeNameAttribute()
    {
    return removeAttribute( "name" );
    }
  
  public String getIdAttribute()
    {
    return getAttribute( "id" );
    }
  
  public void setIdAttribute( String value )
    {
    setAttribute( "id", value );
    }
  
  public String removeIdAttribute()
    {
    return removeAttribute( "id" );
    }
  
  public String getForeigntaskAttribute()
    {
    return getAttribute( "foreigntask" );
    }
  
  public void setForeigntaskAttribute( String value )
    {
    setAttribute( "foreigntask", value );
    }
  
  public String removeForeigntaskAttribute()
    {
    return removeAttribute( "foreigntask" );
    }
  
  public String getInarcsAttribute()
    {
    return getAttribute( "inarcs" );
    }
  
  public void setInarcsAttribute( String value )
    {
    setAttribute( "inarcs", value );
    }
  
  public String removeInarcsAttribute()
    {
    return removeAttribute( "inarcs" );
    }
  
  public Vector getInarcsReference( IIDRefBinding xml )
    {
    Vector v = new Vector();
    String keys = (String) _Attributes.get( "inarcs" );

    if( keys != null )
      {
      for( Enumeration e = new StringTokenizer( keys ); e.hasMoreElements(); )
        v.addElement( xml.getIdRef( (String) e.nextElement() ) );
      }

    return v;
    }
  
  public String getYAttribute()
    {
    return getAttribute( "y" );
    }
  
  public void setYAttribute( String value )
    {
    setAttribute( "y", value );
    }
  
  public String removeYAttribute()
    {
    return removeAttribute( "y" );
    }
  
  public String getTimeoutAttribute()
    {
    return getAttribute( "timeout" );
    }
  
  public void setTimeoutAttribute( String value )
    {
    setAttribute( "timeout", value );
    }
  
  public String removeTimeoutAttribute()
    {
    return removeAttribute( "timeout" );
    }
  
  public String getXAttribute()
    {
    return getAttribute( "x" );
    }
  
  public void setXAttribute( String value )
    {
    setAttribute( "x", value );
    }
  
  public String removeXAttribute()
    {
    return removeAttribute( "x" );
    }
  
  public String getSecuritydomainurlAttribute()
    {
    return getAttribute( "securitydomainurl" );
    }
  
  public void setSecuritydomainurlAttribute( String value )
    {
    setAttribute( "securitydomainurl", value );
    }
  
  public String removeSecuritydomainurlAttribute()
    {
    return removeAttribute( "securitydomainurl" );
    }
  
  public String getHostAttribute()
    {
    return getAttribute( "host" );
    }
  
  public void setHostAttribute( String value )
    {
    setAttribute( "host", value );
    }
  
  public String removeHostAttribute()
    {
    return removeAttribute( "host" );
    }

  // element Description
  
  public String getDescription()
    {
    return _Description == null ? null : _Description;
    }
  
  public void setDescription( String arg0 )
    {
    _Description = arg0 == null ? null : new String( arg0 );
    }
  
  public void removeDescription()
    {
    _Description = null;
    }

  // element Roles
  
  public void addRoles( IRoles arg0  )
    {
    if( _Roles != null )
      _Roles.addElement( arg0 );
    }
  
  public int getRolesCount()
    {
    return _Roles == null ? 0 : _Roles.size();
    }
  
  public void setRoless( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Roles = null;
      return;
      }

    _Roles = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Roles.addElement( string );
      }
    }
  
  public IRoles[] getRoless()
    {
    if( _Roles == null )
      return null;

    IRoles[] array = new IRoles[ _Roles.size() ];
    _Roles.copyInto( array );

    return array;
    }
  
  public void setRoless( IRoles[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Roles = v ;
    }
  
  public Enumeration getRolesElements()
    {
    return _Roles == null ? null : _Roles.elements();
    }
  
  public IRoles getRolesAt( int arg0 )
    {
    return _Roles == null ? null :  (IRoles) _Roles.elementAt( arg0 );
    }
  
  public void insertRolesAt( IRoles arg0, int arg1 )
    {
    if( _Roles != null )
      _Roles.insertElementAt( arg0, arg1 );
    }
  
  public void setRolesAt( IRoles arg0, int arg1 )
    {
    if( _Roles != null )
      _Roles.setElementAt( arg0, arg1 );
    }
  
  public boolean removeRoles( IRoles arg0 )
    {
    return _Roles == null ? false : _Roles.removeElement( arg0 );
    }
  
  public void removeRolesAt( int arg0 )
    {
    if( _Roles == null )
      return;

    _Roles.removeElementAt( arg0 );
    }
  
  public void removeAllRoless()
    {
    if( _Roles == null )
      return;

    _Roles.removeAllElements();
    }

  // element Realization
  
  public IRealization getRealization()
    {
    return _Realization;
    }
  
  public void setRealization( IRealization arg0 )
    {
    _Realization = arg0;
    }

  // element Invocation
  
  public void addInvocation( IInvocation arg0  )
    {
    if( _Invocation != null )
      _Invocation.addElement( arg0 );
    }
  
  public int getInvocationCount()
    {
    return _Invocation == null ? 0 : _Invocation.size();
    }
  
  public void setInvocations( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Invocation = null;
      return;
      }

    _Invocation = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Invocation.addElement( string );
      }
    }
  
  public IInvocation[] getInvocations()
    {
    if( _Invocation == null )
      return null;

    IInvocation[] array = new IInvocation[ _Invocation.size() ];
    _Invocation.copyInto( array );

    return array;
    }
  
  public void setInvocations( IInvocation[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Invocation = v ;
    }
  
  public Enumeration getInvocationElements()
    {
    return _Invocation == null ? null : _Invocation.elements();
    }
  
  public IInvocation getInvocationAt( int arg0 )
    {
    return _Invocation == null ? null :  (IInvocation) _Invocation.elementAt( arg0 );
    }
  
  public void insertInvocationAt( IInvocation arg0, int arg1 )
    {
    if( _Invocation != null )
      _Invocation.insertElementAt( arg0, arg1 );
    }
  
  public void setInvocationAt( IInvocation arg0, int arg1 )
    {
    if( _Invocation != null )
      _Invocation.setElementAt( arg0, arg1 );
    }
  
  public boolean removeInvocation( IInvocation arg0 )
    {
    return _Invocation == null ? false : _Invocation.removeElement( arg0 );
    }
  
  public void removeInvocationAt( int arg0 )
    {
    if( _Invocation == null )
      return;

    _Invocation.removeElementAt( arg0 );
    }
  
  public void removeAllInvocations()
    {
    if( _Invocation == null )
      return;

    _Invocation.removeAllElements();
    }

  // element Output
  
  public void addOutput( IOutput arg0  )
    {
    if( _Output != null )
      _Output.addElement( arg0 );
    }
  
  public int getOutputCount()
    {
    return _Output == null ? 0 : _Output.size();
    }
  
  public void setOutputs( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Output = null;
      return;
      }

    _Output = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Output.addElement( string );
      }
    }
  
  public IOutput[] getOutputs()
    {
    if( _Output == null )
      return null;

    IOutput[] array = new IOutput[ _Output.size() ];
    _Output.copyInto( array );

    return array;
    }
  
  public void setOutputs( IOutput[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Output = v ;
    }
  
  public Enumeration getOutputElements()
    {
    return _Output == null ? null : _Output.elements();
    }
  
  public IOutput getOutputAt( int arg0 )
    {
    return _Output == null ? null :  (IOutput) _Output.elementAt( arg0 );
    }
  
  public void insertOutputAt( IOutput arg0, int arg1 )
    {
    if( _Output != null )
      _Output.insertElementAt( arg0, arg1 );
    }
  
  public void setOutputAt( IOutput arg0, int arg1 )
    {
    if( _Output != null )
      _Output.setElementAt( arg0, arg1 );
    }
  
  public boolean removeOutput( IOutput arg0 )
    {
    return _Output == null ? false : _Output.removeElement( arg0 );
    }
  
  public void removeOutputAt( int arg0 )
    {
    if( _Output == null )
      return;

    _Output.removeElementAt( arg0 );
    }
  
  public void removeAllOutputs()
    {
    if( _Output == null )
      return;

    _Output.removeAllElements();
    }

  // element TaskException
  
  public void addTaskException( ITaskException arg0  )
    {
    if( _TaskException != null )
      _TaskException.addElement( arg0 );
    }
  
  public int getTaskExceptionCount()
    {
    return _TaskException == null ? 0 : _TaskException.size();
    }
  
  public void setTaskExceptions( Vector arg0 )
    {
    if( arg0 == null )
      {
      _TaskException = null;
      return;
      }

    _TaskException = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _TaskException.addElement( string );
      }
    }
  
  public ITaskException[] getTaskExceptions()
    {
    if( _TaskException == null )
      return null;

    ITaskException[] array = new ITaskException[ _TaskException.size() ];
    _TaskException.copyInto( array );

    return array;
    }
  
  public void setTaskExceptions( ITaskException[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _TaskException = v ;
    }
  
  public Enumeration getTaskExceptionElements()
    {
    return _TaskException == null ? null : _TaskException.elements();
    }
  
  public ITaskException getTaskExceptionAt( int arg0 )
    {
    return _TaskException == null ? null :  (ITaskException) _TaskException.elementAt( arg0 );
    }
  
  public void insertTaskExceptionAt( ITaskException arg0, int arg1 )
    {
    if( _TaskException != null )
      _TaskException.insertElementAt( arg0, arg1 );
    }
  
  public void setTaskExceptionAt( ITaskException arg0, int arg1 )
    {
    if( _TaskException != null )
      _TaskException.setElementAt( arg0, arg1 );
    }
  
  public boolean removeTaskException( ITaskException arg0 )
    {
    return _TaskException == null ? false : _TaskException.removeElement( arg0 );
    }
  
  public void removeTaskExceptionAt( int arg0 )
    {
    if( _TaskException == null )
      return;

    _TaskException.removeElementAt( arg0 );
    }
  
  public void removeAllTaskExceptions()
    {
    if( _TaskException == null )
      return;

    _TaskException.removeAllElements();
    }

  // element DataSecurityMask
  
  public void addDataSecurityMask( IDataSecurityMask arg0  )
    {
    if( _DataSecurityMask != null )
      _DataSecurityMask.addElement( arg0 );
    }
  
  public int getDataSecurityMaskCount()
    {
    return _DataSecurityMask == null ? 0 : _DataSecurityMask.size();
    }
  
  public void setDataSecurityMasks( Vector arg0 )
    {
    if( arg0 == null )
      {
      _DataSecurityMask = null;
      return;
      }

    _DataSecurityMask = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _DataSecurityMask.addElement( string );
      }
    }
  
  public IDataSecurityMask[] getDataSecurityMasks()
    {
    if( _DataSecurityMask == null )
      return null;

    IDataSecurityMask[] array = new IDataSecurityMask[ _DataSecurityMask.size() ];
    _DataSecurityMask.copyInto( array );

    return array;
    }
  
  public void setDataSecurityMasks( IDataSecurityMask[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _DataSecurityMask = v ;
    }
  
  public Enumeration getDataSecurityMaskElements()
    {
    return _DataSecurityMask == null ? null : _DataSecurityMask.elements();
    }
  
  public IDataSecurityMask getDataSecurityMaskAt( int arg0 )
    {
    return _DataSecurityMask == null ? null :  (IDataSecurityMask) _DataSecurityMask.elementAt( arg0 );
    }
  
  public void insertDataSecurityMaskAt( IDataSecurityMask arg0, int arg1 )
    {
    if( _DataSecurityMask != null )
      _DataSecurityMask.insertElementAt( arg0, arg1 );
    }
  
  public void setDataSecurityMaskAt( IDataSecurityMask arg0, int arg1 )
    {
    if( _DataSecurityMask != null )
      _DataSecurityMask.setElementAt( arg0, arg1 );
    }
  
  public boolean removeDataSecurityMask( IDataSecurityMask arg0 )
    {
    return _DataSecurityMask == null ? false : _DataSecurityMask.removeElement( arg0 );
    }
  
  public void removeDataSecurityMaskAt( int arg0 )
    {
    if( _DataSecurityMask == null )
      return;

    _DataSecurityMask.removeElementAt( arg0 );
    }
  
  public void removeAllDataSecurityMasks()
    {
    if( _DataSecurityMask == null )
      return;

    _DataSecurityMask.removeAllElements();
    }

  // element Constraint
  
  public void addConstraint( String arg0  )
    {
    if( _Constraint != null )
      _Constraint.addElement( arg0 == null ? null : new String( arg0 ) );
    }
  
  public int getConstraintCount()
    {
    return _Constraint == null ? 0 : _Constraint.size();
    }
  
  public void setConstraints( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Constraint = null;
      return;
      }

    _Constraint = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Constraint.addElement( string == null ? null : new String( string ) );
      }
    }
  
  public String[] getConstraints()
    {
    if( _Constraint == null )
      return null;

    String[] array = new String[ _Constraint.size() ];
    int i = 0;

    for( Enumeration e = _Constraint.elements(); e.hasMoreElements(); i++ )
      array[ i ] = ((String) e.nextElement());

    return array;
    }
  
  public void setConstraints( String[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] == null ? null : new String( arg0[ i ] ) );
      }

    _Constraint = v ;
    }
  
  public Enumeration getConstraintElements()
    {
    if( _Constraint == null )
      return null;

    Vector v = new Vector();

    for( Enumeration e = _Constraint.elements(); e.hasMoreElements(); )
      v.addElement( ((String) e.nextElement()) );

    return v.elements();
    }
  
  public String getConstraintAt( int arg0 )
    {
    return _Constraint == null ? null :  ((String) _Constraint.elementAt( arg0 ));
    }
  
  public void insertConstraintAt( String arg0, int arg1 )
    {
    if( _Constraint != null )
      _Constraint.insertElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public void setConstraintAt( String arg0, int arg1 )
    {
    if( _Constraint != null )
      _Constraint.setElementAt( arg0 == null ? null : new String( arg0 ), arg1 );
    }
  
  public boolean removeConstraint( String arg0 )
    {
    if( _Constraint == null )
      return false;

    int i = 0;

    for( Enumeration e = _Constraint.elements(); e.hasMoreElements(); i++ )
      if( ((String) e.nextElement()).equals( arg0 ) )
        {
        _Constraint.removeElementAt( i );
        return true;
        }

    return false;
    }
  
  public void removeConstraintAt( int arg0 )
    {
    if( _Constraint == null )
      return;

    _Constraint.removeElementAt( arg0 );
    }
  
  public void removeAllConstraints()
    {
    if( _Constraint == null )
      return;

    _Constraint.removeAllElements();
    }

  // element InputOperator
  
  public IInputOperator getInputOperator()
    {
    return _InputOperator;
    }
  
  public void setInputOperator( IInputOperator arg0 )
    {
    _InputOperator = arg0;
    }
  
  public void removeInputOperator()
    {
    _InputOperator = null;
    }

  // element OutputOperator
  
  public IOutputOperator getOutputOperator()
    {
    return _OutputOperator;
    }
  
  public void setOutputOperator( IOutputOperator arg0 )
    {
    _OutputOperator = arg0;
    }
  
  public void removeOutputOperator()
    {
    _OutputOperator = null;
    }
  }