/**
 * ITask.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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
import org.ofbiz.wrappers.xml.IIDRefBinding;

public interface ITask extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getOutarcsAttribute();
  public void setOutarcsAttribute( String value );
  public String removeOutarcsAttribute();
  public Vector getOutarcsReference( IIDRefBinding xml );
  public String getNameAttribute();
  public void setNameAttribute( String value );
  public String removeNameAttribute();
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();
  public String getForeigntaskAttribute();
  public void setForeigntaskAttribute( String value );
  public String removeForeigntaskAttribute();
  public String getInarcsAttribute();
  public void setInarcsAttribute( String value );
  public String removeInarcsAttribute();
  public Vector getInarcsReference( IIDRefBinding xml );
  public String getYAttribute();
  public void setYAttribute( String value );
  public String removeYAttribute();
  public String getTimeoutAttribute();
  public void setTimeoutAttribute( String value );
  public String removeTimeoutAttribute();
  public String getXAttribute();
  public void setXAttribute( String value );
  public String removeXAttribute();
  public String getSecuritydomainurlAttribute();
  public void setSecuritydomainurlAttribute( String value );
  public String removeSecuritydomainurlAttribute();
  public String getHostAttribute();
  public void setHostAttribute( String value );
  public String removeHostAttribute();

  // element Description
  public String getDescription();
  public void setDescription( String arg0 );
  public void removeDescription();

  // element Roles
  public void addRoles( IRoles arg0  );
  public int getRolesCount();
  public void setRoless( Vector arg0 );
  public IRoles[] getRoless();
  public void setRoless( IRoles[] arg0 );
  public Enumeration getRolesElements();
  public IRoles getRolesAt( int arg0 );
  public void insertRolesAt( IRoles arg0, int arg1 );
  public void setRolesAt( IRoles arg0, int arg1 );
  public boolean removeRoles( IRoles arg0 );
  public void removeRolesAt( int arg0 );
  public void removeAllRoless();

  // element Realization
  public IRealization getRealization();
  public void setRealization( IRealization arg0 );

  // element Invocation
  public void addInvocation( IInvocation arg0  );
  public int getInvocationCount();
  public void setInvocations( Vector arg0 );
  public IInvocation[] getInvocations();
  public void setInvocations( IInvocation[] arg0 );
  public Enumeration getInvocationElements();
  public IInvocation getInvocationAt( int arg0 );
  public void insertInvocationAt( IInvocation arg0, int arg1 );
  public void setInvocationAt( IInvocation arg0, int arg1 );
  public boolean removeInvocation( IInvocation arg0 );
  public void removeInvocationAt( int arg0 );
  public void removeAllInvocations();

  // element Output
  public void addOutput( IOutput arg0  );
  public int getOutputCount();
  public void setOutputs( Vector arg0 );
  public IOutput[] getOutputs();
  public void setOutputs( IOutput[] arg0 );
  public Enumeration getOutputElements();
  public IOutput getOutputAt( int arg0 );
  public void insertOutputAt( IOutput arg0, int arg1 );
  public void setOutputAt( IOutput arg0, int arg1 );
  public boolean removeOutput( IOutput arg0 );
  public void removeOutputAt( int arg0 );
  public void removeAllOutputs();

  // element TaskException
  public void addTaskException( ITaskException arg0  );
  public int getTaskExceptionCount();
  public void setTaskExceptions( Vector arg0 );
  public ITaskException[] getTaskExceptions();
  public void setTaskExceptions( ITaskException[] arg0 );
  public Enumeration getTaskExceptionElements();
  public ITaskException getTaskExceptionAt( int arg0 );
  public void insertTaskExceptionAt( ITaskException arg0, int arg1 );
  public void setTaskExceptionAt( ITaskException arg0, int arg1 );
  public boolean removeTaskException( ITaskException arg0 );
  public void removeTaskExceptionAt( int arg0 );
  public void removeAllTaskExceptions();

  // element DataSecurityMask
  public void addDataSecurityMask( IDataSecurityMask arg0  );
  public int getDataSecurityMaskCount();
  public void setDataSecurityMasks( Vector arg0 );
  public IDataSecurityMask[] getDataSecurityMasks();
  public void setDataSecurityMasks( IDataSecurityMask[] arg0 );
  public Enumeration getDataSecurityMaskElements();
  public IDataSecurityMask getDataSecurityMaskAt( int arg0 );
  public void insertDataSecurityMaskAt( IDataSecurityMask arg0, int arg1 );
  public void setDataSecurityMaskAt( IDataSecurityMask arg0, int arg1 );
  public boolean removeDataSecurityMask( IDataSecurityMask arg0 );
  public void removeDataSecurityMaskAt( int arg0 );
  public void removeAllDataSecurityMasks();

  // element Constraint
  public void addConstraint( String arg0  );
  public int getConstraintCount();
  public void setConstraints( Vector arg0 );
  public String[] getConstraints();
  public void setConstraints( String[] arg0 );
  public Enumeration getConstraintElements();
  public String getConstraintAt( int arg0 );
  public void insertConstraintAt( String arg0, int arg1 );
  public void setConstraintAt( String arg0, int arg1 );
  public boolean removeConstraint( String arg0 );
  public void removeConstraintAt( int arg0 );
  public void removeAllConstraints();

  // element InputOperator
  public IInputOperator getInputOperator();
  public void setInputOperator( IInputOperator arg0 );
  public void removeInputOperator();

  // element OutputOperator
  public IOutputOperator getOutputOperator();
  public void setOutputOperator( IOutputOperator arg0 );
  public void removeOutputOperator();
  }