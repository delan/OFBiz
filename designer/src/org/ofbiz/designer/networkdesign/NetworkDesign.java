/**
 * NetworkDesign.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class NetworkDesign implements INetworkDesign
  {
  public Vector _Task = new Vector();
  public Vector _Arc = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.NetworkDesign" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Task
  
  public void addTask( ITask arg0  )
    {
    if( _Task != null )
      _Task.addElement( arg0 );
    }
  
  public int getTaskCount()
    {
    return _Task == null ? 0 : _Task.size();
    }
  
  public void setTasks( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Task = null;
      return;
      }

    _Task = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Task.addElement( string );
      }
    }
  
  public ITask[] getTasks()
    {
    if( _Task == null )
      return null;

    ITask[] array = new ITask[ _Task.size() ];
    _Task.copyInto( array );

    return array;
    }
  
  public void setTasks( ITask[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Task = v ;
    }
  
  public Enumeration getTaskElements()
    {
    return _Task == null ? null : _Task.elements();
    }
  
  public ITask getTaskAt( int arg0 )
    {
    return _Task == null ? null :  (ITask) _Task.elementAt( arg0 );
    }
  
  public void insertTaskAt( ITask arg0, int arg1 )
    {
    if( _Task != null )
      _Task.insertElementAt( arg0, arg1 );
    }
  
  public void setTaskAt( ITask arg0, int arg1 )
    {
    if( _Task != null )
      _Task.setElementAt( arg0, arg1 );
    }
  
  public boolean removeTask( ITask arg0 )
    {
    return _Task == null ? false : _Task.removeElement( arg0 );
    }
  
  public void removeTaskAt( int arg0 )
    {
    if( _Task == null )
      return;

    _Task.removeElementAt( arg0 );
    }
  
  public void removeAllTasks()
    {
    if( _Task == null )
      return;

    _Task.removeAllElements();
    }

  // element Arc
  
  public void addArc( IArc arg0  )
    {
    if( _Arc != null )
      _Arc.addElement( arg0 );
    }
  
  public int getArcCount()
    {
    return _Arc == null ? 0 : _Arc.size();
    }
  
  public void setArcs( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Arc = null;
      return;
      }

    _Arc = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Arc.addElement( string );
      }
    }
  
  public IArc[] getArcs()
    {
    if( _Arc == null )
      return null;

    IArc[] array = new IArc[ _Arc.size() ];
    _Arc.copyInto( array );

    return array;
    }
  
  public void setArcs( IArc[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Arc = v ;
    }
  
  public Enumeration getArcElements()
    {
    return _Arc == null ? null : _Arc.elements();
    }
  
  public IArc getArcAt( int arg0 )
    {
    return _Arc == null ? null :  (IArc) _Arc.elementAt( arg0 );
    }
  
  public void insertArcAt( IArc arg0, int arg1 )
    {
    if( _Arc != null )
      _Arc.insertElementAt( arg0, arg1 );
    }
  
  public void setArcAt( IArc arg0, int arg1 )
    {
    if( _Arc != null )
      _Arc.setElementAt( arg0, arg1 );
    }
  
  public boolean removeArc( IArc arg0 )
    {
    return _Arc == null ? false : _Arc.removeElement( arg0 );
    }
  
  public void removeArcAt( int arg0 )
    {
    if( _Arc == null )
      return;

    _Arc.removeElementAt( arg0 );
    }
  
  public void removeAllArcs()
    {
    if( _Arc == null )
      return;

    _Arc.removeAllElements();
    }
  }