/**
 * SimpleRealization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class SimpleRealization implements ISimpleRealization
  {
  public ICollaborationRealization _CollaborationRealization = null;
  public IHumanRealization _HumanRealization = null;
  public INonTransactionalTaskRealization _NonTransactionalTaskRealization = null;
  public ITransactionalTaskRealization _TransactionalTaskRealization = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.SimpleRealization" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element CollaborationRealization
  
  public ICollaborationRealization getCollaborationRealization()
    {
    return _CollaborationRealization;
    }
  
  public void setCollaborationRealization( ICollaborationRealization arg0 )
    {
    _HumanRealization = null;
    _NonTransactionalTaskRealization = null;
    _TransactionalTaskRealization = null;

    _CollaborationRealization = arg0;
    }

  // element HumanRealization
  
  public IHumanRealization getHumanRealization()
    {
    return _HumanRealization;
    }
  
  public void setHumanRealization( IHumanRealization arg0 )
    {
    _CollaborationRealization = null;
    _NonTransactionalTaskRealization = null;
    _TransactionalTaskRealization = null;

    _HumanRealization = arg0;
    }

  // element NonTransactionalTaskRealization
  
  public INonTransactionalTaskRealization getNonTransactionalTaskRealization()
    {
    return _NonTransactionalTaskRealization;
    }
  
  public void setNonTransactionalTaskRealization( INonTransactionalTaskRealization arg0 )
    {
    _CollaborationRealization = null;
    _HumanRealization = null;
    _TransactionalTaskRealization = null;

    _NonTransactionalTaskRealization = arg0;
    }

  // element TransactionalTaskRealization
  
  public ITransactionalTaskRealization getTransactionalTaskRealization()
    {
    return _TransactionalTaskRealization;
    }
  
  public void setTransactionalTaskRealization( ITransactionalTaskRealization arg0 )
    {
    _CollaborationRealization = null;
    _HumanRealization = null;
    _NonTransactionalTaskRealization = null;

    _TransactionalTaskRealization = arg0;
    }
  }