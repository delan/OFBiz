/**
 * DomainEnv.java	Java 1.3.0 Fri Apr 27 15:06:33 EDT 2001
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

package org.ofbiz.designer.domainenv;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class DomainEnv implements IDomainEnv
  {
  public Hashtable _Attributes = new Hashtable();
  public String _Name = null;
  public Vector _DomainInfo = new Vector();
  public Vector _PolicyRecord = new Vector();
  public Vector _DomainRelationship = new Vector();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.domainenv.DomainEnv" );
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
  
  public String getDateAttribute()
    {
    return getAttribute( "date" );
    }
  
  public void setDateAttribute( String value )
    {
    setAttribute( "date", value );
    }
  
  public String removeDateAttribute()
    {
    return removeAttribute( "date" );
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

  // element Name
  
  public String getName()
    {
    return _Name == null ? null : _Name;
    }
  
  public void setName( String arg0 )
    {
    _Name = arg0 == null ? null : new String( arg0 );
    }

  // element DomainInfo
  
  public void addDomainInfo( IDomainInfo arg0  )
    {
    if( _DomainInfo != null )
      _DomainInfo.addElement( arg0 );
    }
  
  public int getDomainInfoCount()
    {
    return _DomainInfo == null ? 0 : _DomainInfo.size();
    }
  
  public void setDomainInfos( Vector arg0 )
    {
    if( arg0 == null )
      {
      _DomainInfo = null;
      return;
      }

    _DomainInfo = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _DomainInfo.addElement( string );
      }
    }
  
  public IDomainInfo[] getDomainInfos()
    {
    if( _DomainInfo == null )
      return null;

    IDomainInfo[] array = new IDomainInfo[ _DomainInfo.size() ];
    _DomainInfo.copyInto( array );

    return array;
    }
  
  public void setDomainInfos( IDomainInfo[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _DomainInfo = v ;
    }
  
  public Enumeration getDomainInfoElements()
    {
    return _DomainInfo == null ? null : _DomainInfo.elements();
    }
  
  public IDomainInfo getDomainInfoAt( int arg0 )
    {
    return _DomainInfo == null ? null :  (IDomainInfo) _DomainInfo.elementAt( arg0 );
    }
  
  public void insertDomainInfoAt( IDomainInfo arg0, int arg1 )
    {
    if( _DomainInfo != null )
      _DomainInfo.insertElementAt( arg0, arg1 );
    }
  
  public void setDomainInfoAt( IDomainInfo arg0, int arg1 )
    {
    if( _DomainInfo != null )
      _DomainInfo.setElementAt( arg0, arg1 );
    }
  
  public boolean removeDomainInfo( IDomainInfo arg0 )
    {
    return _DomainInfo == null ? false : _DomainInfo.removeElement( arg0 );
    }
  
  public void removeDomainInfoAt( int arg0 )
    {
    if( _DomainInfo == null )
      return;

    _DomainInfo.removeElementAt( arg0 );
    }
  
  public void removeAllDomainInfos()
    {
    if( _DomainInfo == null )
      return;

    _DomainInfo.removeAllElements();
    }

  // element PolicyRecord
  
  public void addPolicyRecord( IPolicyRecord arg0  )
    {
    if( _PolicyRecord != null )
      _PolicyRecord.addElement( arg0 );
    }
  
  public int getPolicyRecordCount()
    {
    return _PolicyRecord == null ? 0 : _PolicyRecord.size();
    }
  
  public void setPolicyRecords( Vector arg0 )
    {
    if( arg0 == null )
      {
      _PolicyRecord = null;
      return;
      }

    _PolicyRecord = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _PolicyRecord.addElement( string );
      }
    }
  
  public IPolicyRecord[] getPolicyRecords()
    {
    if( _PolicyRecord == null )
      return null;

    IPolicyRecord[] array = new IPolicyRecord[ _PolicyRecord.size() ];
    _PolicyRecord.copyInto( array );

    return array;
    }
  
  public void setPolicyRecords( IPolicyRecord[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _PolicyRecord = v ;
    }
  
  public Enumeration getPolicyRecordElements()
    {
    return _PolicyRecord == null ? null : _PolicyRecord.elements();
    }
  
  public IPolicyRecord getPolicyRecordAt( int arg0 )
    {
    return _PolicyRecord == null ? null :  (IPolicyRecord) _PolicyRecord.elementAt( arg0 );
    }
  
  public void insertPolicyRecordAt( IPolicyRecord arg0, int arg1 )
    {
    if( _PolicyRecord != null )
      _PolicyRecord.insertElementAt( arg0, arg1 );
    }
  
  public void setPolicyRecordAt( IPolicyRecord arg0, int arg1 )
    {
    if( _PolicyRecord != null )
      _PolicyRecord.setElementAt( arg0, arg1 );
    }
  
  public boolean removePolicyRecord( IPolicyRecord arg0 )
    {
    return _PolicyRecord == null ? false : _PolicyRecord.removeElement( arg0 );
    }
  
  public void removePolicyRecordAt( int arg0 )
    {
    if( _PolicyRecord == null )
      return;

    _PolicyRecord.removeElementAt( arg0 );
    }
  
  public void removeAllPolicyRecords()
    {
    if( _PolicyRecord == null )
      return;

    _PolicyRecord.removeAllElements();
    }

  // element DomainRelationship
  
  public void addDomainRelationship( IDomainRelationship arg0  )
    {
    if( _DomainRelationship != null )
      _DomainRelationship.addElement( arg0 );
    }
  
  public int getDomainRelationshipCount()
    {
    return _DomainRelationship == null ? 0 : _DomainRelationship.size();
    }
  
  public void setDomainRelationships( Vector arg0 )
    {
    if( arg0 == null )
      {
      _DomainRelationship = null;
      return;
      }

    _DomainRelationship = new Vector();

    for( Enumeration e = arg0.elements(); e.hasMoreElements(); )
      {
      String string = (String) e.nextElement();
      _DomainRelationship.addElement( string );
      }
    }
  
  public IDomainRelationship[] getDomainRelationships()
    {
    if( _DomainRelationship == null )
      return null;

    IDomainRelationship[] array = new IDomainRelationship[ _DomainRelationship.size() ];
    _DomainRelationship.copyInto( array );

    return array;
    }
  
  public void setDomainRelationships( IDomainRelationship[] arg0 )
    {
    Vector v = arg0 == null ? null : new Vector();

    if( arg0 != null )
      {
      for( int i = 0; i < arg0.length; i++ )
        v.addElement( arg0[ i ] );
      }

    _DomainRelationship = v ;
    }
  
  public Enumeration getDomainRelationshipElements()
    {
    return _DomainRelationship == null ? null : _DomainRelationship.elements();
    }
  
  public IDomainRelationship getDomainRelationshipAt( int arg0 )
    {
    return _DomainRelationship == null ? null :  (IDomainRelationship) _DomainRelationship.elementAt( arg0 );
    }
  
  public void insertDomainRelationshipAt( IDomainRelationship arg0, int arg1 )
    {
    if( _DomainRelationship != null )
      _DomainRelationship.insertElementAt( arg0, arg1 );
    }
  
  public void setDomainRelationshipAt( IDomainRelationship arg0, int arg1 )
    {
    if( _DomainRelationship != null )
      _DomainRelationship.setElementAt( arg0, arg1 );
    }
  
  public boolean removeDomainRelationship( IDomainRelationship arg0 )
    {
    return _DomainRelationship == null ? false : _DomainRelationship.removeElement( arg0 );
    }
  
  public void removeDomainRelationshipAt( int arg0 )
    {
    if( _DomainRelationship == null )
      return;

    _DomainRelationship.removeElementAt( arg0 );
    }
  
  public void removeAllDomainRelationships()
    {
    if( _DomainRelationship == null )
      return;

    _DomainRelationship.removeAllElements();
    }
  }