/**
 * Arc.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class Arc implements IArc
  {
  public Hashtable _Attributes = new Hashtable();
  public IBoundaryInfo _BoundaryInfo = null;
  public Vector _Mapping = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.Arc" );
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
  
  public String getDestinationAttribute()
    {
    return getAttribute( "destination" );
    }
  
  public void setDestinationAttribute( String value )
    {
    setAttribute( "destination", value );
    }
  
  public String removeDestinationAttribute()
    {
    return removeAttribute( "destination" );
    }
  
  public Object getDestinationReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "destination" );
    return key == null ? null : xml.getIdRef( key );
    }
  
  public String getArctypeAttribute()
    {
    return getAttribute( "arctype" );
    }
  
  public void setArctypeAttribute( String value )
    {
    setAttribute( "arctype", value );
    }
  
  public String removeArctypeAttribute()
    {
    return removeAttribute( "arctype" );
    }
  
  public String getExceptionAttribute()
    {
    return getAttribute( "exception" );
    }
  
  public void setExceptionAttribute( String value )
    {
    setAttribute( "exception", value );
    }
  
  public String removeExceptionAttribute()
    {
    return removeAttribute( "exception" );
    }
  
  public String getSourceAttribute()
    {
    return getAttribute( "source" );
    }
  
  public void setSourceAttribute( String value )
    {
    setAttribute( "source", value );
    }
  
  public String removeSourceAttribute()
    {
    return removeAttribute( "source" );
    }
  
  public Object getSourceReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "source" );
    return key == null ? null : xml.getIdRef( key );
    }
  
  public String getAlternativetransitionAttribute()
    {
    return getAttribute( "alternativetransition" );
    }
  
  public void setAlternativetransitionAttribute( String value )
    {
    setAttribute( "alternativetransition", value );
    }
  
  public String removeAlternativetransitionAttribute()
    {
    return removeAttribute( "alternativetransition" );
    }
  
  public Object getAlternativetransitionReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "alternativetransition" );
    return key == null ? null : xml.getIdRef( key );
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

  // element BoundaryInfo
  
  public IBoundaryInfo getBoundaryInfo()
    {
    return _BoundaryInfo;
    }
  
  public void setBoundaryInfo( IBoundaryInfo arg0 )
    {
    _BoundaryInfo = arg0;
    }
  
  public void removeBoundaryInfo()
    {
    _BoundaryInfo = null;
    }

  // element Mapping
  
  public void addMapping( IMapping arg0  )
    {
    if( _Mapping != null )
      _Mapping.addElement( arg0 );
    }
  
  public int getMappingCount()
    {
    return _Mapping == null ? 0 : _Mapping.size();
    }
  
  public void setMappings( Vector arg0 )
    {
    if( arg0 == null )
      {
      _Mapping = null;
      return;
      }

    _Mapping = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _Mapping.addElement( string );
      }
    }
  
  public IMapping[] getMappings()
    {
    if( _Mapping == null )
      return null;

    IMapping[] array = new IMapping[ _Mapping.size() ];
    _Mapping.copyInto( array );

    return array;
    }
  
  public void setMappings( IMapping[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _Mapping = v ;
    }
  
  public Enumeration getMappingElements()
    {
    return _Mapping == null ? null : _Mapping.elements();
    }
  
  public IMapping getMappingAt( int arg0 )
    {
    return _Mapping == null ? null :  (IMapping) _Mapping.elementAt( arg0 );
    }
  
  public void insertMappingAt( IMapping arg0, int arg1 )
    {
    if( _Mapping != null )
      _Mapping.insertElementAt( arg0, arg1 );
    }
  
  public void setMappingAt( IMapping arg0, int arg1 )
    {
    if( _Mapping != null )
      _Mapping.setElementAt( arg0, arg1 );
    }
  
  public boolean removeMapping( IMapping arg0 )
    {
    return _Mapping == null ? false : _Mapping.removeElement( arg0 );
    }
  
  public void removeMappingAt( int arg0 )
    {
    if( _Mapping == null )
      return;

    _Mapping.removeElementAt( arg0 );
    }
  
  public void removeAllMappings()
    {
    if( _Mapping == null )
      return;

    _Mapping.removeAllElements();
    }
  }