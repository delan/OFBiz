/**
 * Factory.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class Factory
  {
  public static IInputMappingList newInputMappingList()
    {
    return new org.ofbiz.designer.networkdesign.InputMappingList();
    }

  public static IOutput newOutput()
    {
    return new org.ofbiz.designer.networkdesign.Output();
    }

  public static ICorbaMapping newCorbaMapping()
    {
    return new org.ofbiz.designer.networkdesign.CorbaMapping();
    }

  public static IParameter newParameter()
    {
    return new org.ofbiz.designer.networkdesign.Parameter();
    }

  public static IOutputOperator newOutputOperator()
    {
    return new org.ofbiz.designer.networkdesign.OutputOperator();
    }

  public static ISimpleRealization newSimpleRealization()
    {
    return new org.ofbiz.designer.networkdesign.SimpleRealization();
    }

  public static ITaskException newTaskException()
    {
    return new org.ofbiz.designer.networkdesign.TaskException();
    }

  public static IDataSecurityMask newDataSecurityMask()
    {
    return new org.ofbiz.designer.networkdesign.DataSecurityMask();
    }

  public static IInvocation newInvocation()
    {
    return new org.ofbiz.designer.networkdesign.Invocation();
    }

  public static ICorbaInvocation newCorbaInvocation()
    {
    return new org.ofbiz.designer.networkdesign.CorbaInvocation();
    }

  public static IPumpBoundaryInfo newPumpBoundaryInfo()
    {
    return new org.ofbiz.designer.networkdesign.PumpBoundaryInfo();
    }

  public static IArc newArc()
    {
    return new org.ofbiz.designer.networkdesign.Arc();
    }

  public static IOutputMappingList newOutputMappingList()
    {
    return new org.ofbiz.designer.networkdesign.OutputMappingList();
    }

  public static ILocalHandler newLocalHandler()
    {
    return new org.ofbiz.designer.networkdesign.LocalHandler();
    }

  public static IReverseMappingList newReverseMappingList()
    {
    return new org.ofbiz.designer.networkdesign.ReverseMappingList();
    }

  public static IRealization newRealization()
    {
    return new org.ofbiz.designer.networkdesign.Realization();
    }

  public static IOperator newOperator()
    {
    return new org.ofbiz.designer.networkdesign.Operator();
    }

  public static ITransactionalTaskRealization newTransactionalTaskRealization()
    {
    return new org.ofbiz.designer.networkdesign.TransactionalTaskRealization();
    }

  public static IFieldMask newFieldMask()
    {
    return new org.ofbiz.designer.networkdesign.FieldMask();
    }

  public static INonTransactionalTaskRealization newNonTransactionalTaskRealization()
    {
    return new org.ofbiz.designer.networkdesign.NonTransactionalTaskRealization();
    }

  public static IForwardMappingList newForwardMappingList()
    {
    return new org.ofbiz.designer.networkdesign.ForwardMappingList();
    }

  public static INetworkTaskRealization newNetworkTaskRealization()
    {
    return new org.ofbiz.designer.networkdesign.NetworkTaskRealization();
    }

  public static IInputOperator newInputOperator()
    {
    return new org.ofbiz.designer.networkdesign.InputOperator();
    }

  public static IGenericBoundaryInfo newGenericBoundaryInfo()
    {
    return new org.ofbiz.designer.networkdesign.GenericBoundaryInfo();
    }

  public static IMapping newMapping()
    {
    return new org.ofbiz.designer.networkdesign.Mapping();
    }

  public static IRoles newRoles()
    {
    return new org.ofbiz.designer.networkdesign.Roles();
    }

  public static IRole newRole()
    {
    return new org.ofbiz.designer.networkdesign.Role();
    }

  public static ITask newTask()
    {
    return new org.ofbiz.designer.networkdesign.Task();
    }

  public static ICollaborationObject newCollaborationObject()
    {
    return new org.ofbiz.designer.networkdesign.CollaborationObject();
    }

  public static INetworkDesign newNetworkDesign()
    {
    return new org.ofbiz.designer.networkdesign.NetworkDesign();
    }

  public static IBoundaryInfo newBoundaryInfo()
    {
    return new org.ofbiz.designer.networkdesign.BoundaryInfo();
    }

  public static ICompartment newCompartment()
    {
    return new org.ofbiz.designer.networkdesign.Compartment();
    }

  public static ISyncRealization newSyncRealization()
    {
    return new org.ofbiz.designer.networkdesign.SyncRealization();
    }

  public static IDomain newDomain()
    {
    return new org.ofbiz.designer.networkdesign.Domain();
    }

  public static IHumanRealization newHumanRealization()
    {
    return new org.ofbiz.designer.networkdesign.HumanRealization();
    }

  public static ICollaborationRealization newCollaborationRealization()
    {
    return new org.ofbiz.designer.networkdesign.CollaborationRealization();
    }

  public static IField newField()
    {
    return new org.ofbiz.designer.networkdesign.Field();
    }

  }