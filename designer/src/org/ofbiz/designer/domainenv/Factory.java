/**
 * Factory.java	Java 1.3.0 Fri Apr 27 15:06:33 EDT 2001
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

public class Factory
  {
  public static IPosition newPosition()
    {
    return new org.ofbiz.designer.domainenv.Position();
    }

  public static IDomainInfo newDomainInfo()
    {
    return new org.ofbiz.designer.domainenv.DomainInfo();
    }

  public static IDomainRelationship newDomainRelationship()
    {
    return new org.ofbiz.designer.domainenv.DomainRelationship();
    }

  public static IDomainEnv newDomainEnv()
    {
    return new org.ofbiz.designer.domainenv.DomainEnv();
    }

  public static IPolicyRecord newPolicyRecord()
    {
    return new org.ofbiz.designer.domainenv.PolicyRecord();
    }

  public static IColor newColor()
    {
    return new org.ofbiz.designer.domainenv.Color();
    }

  }