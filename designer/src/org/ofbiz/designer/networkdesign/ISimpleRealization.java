/**
 * ISimpleRealization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface ISimpleRealization extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element CollaborationRealization
  public ICollaborationRealization getCollaborationRealization();
  public void setCollaborationRealization( ICollaborationRealization arg0 );

  // element HumanRealization
  public IHumanRealization getHumanRealization();
  public void setHumanRealization( IHumanRealization arg0 );

  // element NonTransactionalTaskRealization
  public INonTransactionalTaskRealization getNonTransactionalTaskRealization();
  public void setNonTransactionalTaskRealization( INonTransactionalTaskRealization arg0 );

  // element TransactionalTaskRealization
  public ITransactionalTaskRealization getTransactionalTaskRealization();
  public void setTransactionalTaskRealization( ITransactionalTaskRealization arg0 );
  }