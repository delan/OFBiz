/**
 * IDomainEnv.java	Java 1.3.0 Fri Apr 27 15:06:33 EDT 2001
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

public interface IDomainEnv extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getDateAttribute();
  public void setDateAttribute( String value );
  public String removeDateAttribute();
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();

  // element Name
  public String getName();
  public void setName( String arg0 );

  // element DomainInfo
  public void addDomainInfo( IDomainInfo arg0  );
  public int getDomainInfoCount();
  public void setDomainInfos( Vector arg0 );
  public IDomainInfo[] getDomainInfos();
  public void setDomainInfos( IDomainInfo[] arg0 );
  public Enumeration getDomainInfoElements();
  public IDomainInfo getDomainInfoAt( int arg0 );
  public void insertDomainInfoAt( IDomainInfo arg0, int arg1 );
  public void setDomainInfoAt( IDomainInfo arg0, int arg1 );
  public boolean removeDomainInfo( IDomainInfo arg0 );
  public void removeDomainInfoAt( int arg0 );
  public void removeAllDomainInfos();

  // element PolicyRecord
  public void addPolicyRecord( IPolicyRecord arg0  );
  public int getPolicyRecordCount();
  public void setPolicyRecords( Vector arg0 );
  public IPolicyRecord[] getPolicyRecords();
  public void setPolicyRecords( IPolicyRecord[] arg0 );
  public Enumeration getPolicyRecordElements();
  public IPolicyRecord getPolicyRecordAt( int arg0 );
  public void insertPolicyRecordAt( IPolicyRecord arg0, int arg1 );
  public void setPolicyRecordAt( IPolicyRecord arg0, int arg1 );
  public boolean removePolicyRecord( IPolicyRecord arg0 );
  public void removePolicyRecordAt( int arg0 );
  public void removeAllPolicyRecords();

  // element DomainRelationship
  public void addDomainRelationship( IDomainRelationship arg0  );
  public int getDomainRelationshipCount();
  public void setDomainRelationships( Vector arg0 );
  public IDomainRelationship[] getDomainRelationships();
  public void setDomainRelationships( IDomainRelationship[] arg0 );
  public Enumeration getDomainRelationshipElements();
  public IDomainRelationship getDomainRelationshipAt( int arg0 );
  public void insertDomainRelationshipAt( IDomainRelationship arg0, int arg1 );
  public void setDomainRelationshipAt( IDomainRelationship arg0, int arg1 );
  public boolean removeDomainRelationship( IDomainRelationship arg0 );
  public void removeDomainRelationshipAt( int arg0 );
  public void removeAllDomainRelationships();
  }