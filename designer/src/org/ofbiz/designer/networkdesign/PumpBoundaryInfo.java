/**
 * PumpBoundaryInfo.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

import java.util.Hashtable;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class PumpBoundaryInfo implements IPumpBoundaryInfo
  {
  public Hashtable _Attributes = new Hashtable();
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.PumpBoundaryInfo" );
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
  
  public String getPumphostAttribute()
    {
    return getAttribute( "pumphost" );
    }
  
  public void setPumphostAttribute( String value )
    {
    setAttribute( "pumphost", value );
    }
  
  public String removePumphostAttribute()
    {
    return removeAttribute( "pumphost" );
    }
  
  public String getReceiverportAttribute()
    {
    return getAttribute( "receiverport" );
    }
  
  public void setReceiverportAttribute( String value )
    {
    setAttribute( "receiverport", value );
    }
  
  public String removeReceiverportAttribute()
    {
    return removeAttribute( "receiverport" );
    }
  
  public String getSenderportAttribute()
    {
    return getAttribute( "senderport" );
    }
  
  public void setSenderportAttribute( String value )
    {
    setAttribute( "senderport", value );
    }
  
  public String removeSenderportAttribute()
    {
    return removeAttribute( "senderport" );
    }
  
  public String getReceiverhostAttribute()
    {
    return getAttribute( "receiverhost" );
    }
  
  public void setReceiverhostAttribute( String value )
    {
    setAttribute( "receiverhost", value );
    }
  
  public String removeReceiverhostAttribute()
    {
    return removeAttribute( "receiverhost" );
    }
  
  public String getPumpportAttribute()
    {
    return getAttribute( "pumpport" );
    }
  
  public void setPumpportAttribute( String value )
    {
    setAttribute( "pumpport", value );
    }
  
  public String removePumpportAttribute()
    {
    return removeAttribute( "pumpport" );
    }
  
  public String getSenderhostAttribute()
    {
    return getAttribute( "senderhost" );
    }
  
  public void setSenderhostAttribute( String value )
    {
    setAttribute( "senderhost", value );
    }
  
  public String removeSenderhostAttribute()
    {
    return removeAttribute( "senderhost" );
    }
  }