/**
 * PolicyRecord.java	Java 1.3.0 Fri Apr 27 15:06:33 EDT 2001
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

import java.util.Hashtable;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IIDRefBinding;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class PolicyRecord implements IPolicyRecord
  {
  public Hashtable _Attributes = new Hashtable();
  public String _PCDATA = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.domainenv.PolicyRecord" );
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
  
  public String getPolicyTypeAttribute()
    {
    return getAttribute( "policyType" );
    }
  
  public void setPolicyTypeAttribute( String value )
    {
    setAttribute( "policyType", value );
    }
  
  public String removePolicyTypeAttribute()
    {
    return removeAttribute( "policyType" );
    }
  
  public String getFromDomainAttribute()
    {
    return getAttribute( "fromDomain" );
    }
  
  public void setFromDomainAttribute( String value )
    {
    setAttribute( "fromDomain", value );
    }
  
  public String removeFromDomainAttribute()
    {
    return removeAttribute( "fromDomain" );
    }
  
  public Object getFromDomainReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "fromDomain" );
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
  
  public String getToDomainAttribute()
    {
    return getAttribute( "toDomain" );
    }
  
  public void setToDomainAttribute( String value )
    {
    setAttribute( "toDomain", value );
    }
  
  public String removeToDomainAttribute()
    {
    return removeAttribute( "toDomain" );
    }
  
  public Object getToDomainReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "toDomain" );
    return key == null ? null : xml.getIdRef( key );
    }

  // element PCDATA
  
  public String getPCDATA()
    {
    return _PCDATA == null ? null : _PCDATA;
    }
  
  public void setPCDATA( String arg0 )
    {
    _PCDATA = arg0 == null ? null : new String( arg0 );
    }
  }